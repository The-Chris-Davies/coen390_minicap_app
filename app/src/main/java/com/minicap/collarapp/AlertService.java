package com.minicap.collarapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nullable;

public class AlertService extends Service {
    private static final String TAG = "AlertService";
    private DocumentReference mDocRef;
    private CollectionReference mTempRef;
    private CollectionReference mExtTempRef;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //compare values
        //eventually, the user will set these in an activity
        final Double highExtTemp = 40.;
        final Double highExtTime = 5.;
        final Double lowExtTemp = -10.;
        final Double lowExtTime = 5.;

        final Double highIntTemp = 40.;
        final Double highIntTime = 5.;
        final Double lowIntTemp = 30.;
        final Double lowIntTime = 5.;


        //init firebase collections
        mDocRef = FirebaseFirestore.getInstance().document("dogs/" + ((intent.hasExtra("dogID")) ? intent.getStringExtra("dogID") : "HpwWiJSGHNbOgJtYi2jM"));
        mTempRef = mDocRef.collection("temperature");
        mExtTempRef = mDocRef.collection("external_temperature");

        //create 'running in background' notification
        Intent notificationIntent = new Intent(this, startingPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "Whistle_Alert")
                .setContentTitle("Whistle Alerts")
                .setContentText("Running in background")
                .setSmallIcon(R.mipmap.ic_launcher_logo)
                .setContentIntent(pendingIntent)
                .build();

        //start service as foreground service
        startForeground(1, notification);

        //create firebase listeners
        //get internal temperature data from firebase
        mTempRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in internal temperature snapshotListener: ", e);
                    return;
                }

                ArrayList<Temperature> internalTemps= new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    //check if value is a string
                    if(documentSnapshot.getData().get("value") instanceof String) {
                        if(((String) documentSnapshot.getData().get("value")).isEmpty())
                            continue;

                        internalTemps.add(new Temperature(
                                Double.parseDouble((String) documentSnapshot.getData().get("value")),
                                (Timestamp) documentSnapshot.getData().get("timestamp")));
                    }
                    else
                        internalTemps.add(documentSnapshot.toObject(Temperature.class));
                Log.d(TAG, "Found " + internalTemps.size() + " internal temperatures in the firebase");

                //if no temperatures available, continue
                if (internalTemps.isEmpty()) {
                    Log.i(TAG, "No internal temperature data available");
                    return;
                }

                //sort temperature list, most recent first
                Collections.sort(internalTemps, Collections.reverseOrder());

                //if last update was more than 15 minutes ago, do nothing (ignore any previous alerts)
                if(Timestamp.now().getSeconds() - 60 * 15 > internalTemps.get(0).getTimestamp().getSeconds())
                    return;

                //flags to indicate hi / lo temp alerts
                Boolean highTempFlag = true, lowTempFlag = true;

                for(int i = 0; i < internalTemps.size(); i++) {
                    //if time between readings is >15 minutes, exit (they're not part of the same walk)
                    if(i >= 1 && (internalTemps.get(i).getTimestamp().getSeconds() + 60*15 < internalTemps.get(i-1).getTimestamp().getSeconds()))
                        break;
                    //if the max flag is still set, and update was over the max, check the timestamp
                    if (highTempFlag && internalTemps.get(i).getValue() > highIntTemp) {
                        if (internalTemps.get(i).getTimestamp().getSeconds() + 60 * highIntTime < internalTemps.get(0).getTimestamp().getSeconds()) {
                            raiseTempAlert(false, true);
                            break;
                        }
                        continue;
                    }
                    highTempFlag = false;

                    //do the same for low temp
                    if(lowTempFlag && internalTemps.get(i).getValue() < lowIntTemp) {
                        if (internalTemps.get(i).getTimestamp().getSeconds() + 60 * lowIntTime < internalTemps.get(0).getTimestamp().getSeconds()) {
                            raiseTempAlert(false, false);
                            break;
                        }
                        continue;
                    }
                    lowTempFlag = false;
                }
            }
        });

        //do the same for the external temperatures
        mExtTempRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in external temperature snapshotListener: ", e);
                    return;
                }

                ArrayList<Temperature> externalTemps= new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    //check if value is a string
                    if(documentSnapshot.getData().get("value") instanceof String) {
                        if(((String) documentSnapshot.getData().get("value")).isEmpty())
                            continue;

                        externalTemps.add(new Temperature(
                                Double.parseDouble((String) documentSnapshot.getData().get("value")),
                                (Timestamp) documentSnapshot.getData().get("timestamp")));
                    }
                    else
                        externalTemps.add(documentSnapshot.toObject(Temperature.class));
                Log.d(TAG, "Found " + externalTemps.size() + " external temperatures in the firebase");

                //if no temperatures available, continue
                if (externalTemps.isEmpty()) {
                    Log.i(TAG, "No external temperature data available");
                    return;
                }

                //sort temperature list, most recent first
                Collections.sort(externalTemps, Collections.reverseOrder());

                //if last update was more than 15 minutes ago, do nothing (ignore any previous alerts)
                if(Timestamp.now().getSeconds() - 60 * 15 > externalTemps.get(0).getTimestamp().getSeconds())
                    return;

                //flags to indicate hi / lo temp alerts
                Boolean highTempFlag = true, lowTempFlag = true;

                for(int i = 0; i < externalTemps.size(); i++) {
                    //if time between readings is >15 minutes, exit (they're not part of the same walk)
                    if(externalTemps.get(i).getTimestamp().getSeconds() + 60*15 < externalTemps.get(i-1).getTimestamp().getSeconds())
                        break;
                    //if the max flag is still set, and update was over the max, check the timestamp
                    if (highTempFlag && externalTemps.get(i).getValue() > highExtTemp) {
                        if (i >= 1 && (externalTemps.get(i).getTimestamp().getSeconds() + 60 * highExtTime < externalTemps.get(0).getTimestamp().getSeconds())) {
                            raiseTempAlert(true, true);
                            break;
                        }
                        continue;
                    }
                    highTempFlag = false;

                    //do the same for low temp
                    if(lowTempFlag && externalTemps.get(i).getValue() < lowExtTemp) {
                        if (externalTemps.get(i).getTimestamp().getSeconds() + 60 * lowExtTime < externalTemps.get(0).getTimestamp().getSeconds()) {
                            raiseTempAlert(true, false);
                            break;
                        }
                        continue;
                    }
                    lowTempFlag = false;
                }
            }
        });

        //todo: add battery alert and watchdog for update rate


        //redeliver intent - when the service is closed, restart it with the same intent
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void raiseTempAlert(boolean isExt, boolean isHigh) {
        final String int_ext = isExt ? "External" : "Body";
        final String hi_lo = isHigh ? "High" : "Low";

        //create alert notification!
        Intent notificationIntent = new Intent(this, startingPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "Whistle_Alert")
                .setContentTitle("Whistle Collar " + hi_lo + " " + int_ext + " Temperature Alert!")
                .setContentText("The " + int_ext + " temperature of your dog is too " + hi_lo + " and has triggered an alert!")
                .setSmallIcon(R.mipmap.ic_launcher_logo)
                .setContentIntent(pendingIntent)
                .build();

        //send the notification!
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, notification);
    }
}