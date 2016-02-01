package com.example.zem.patientcareapp.AlarmModule;

/**
 * Created by Dexter B. on 7/20/2015.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Activities.MainActivity;

/**
 * @author Neel
 *         <p/>
 *         Broadcast reciever, starts when the device gets starts.
 *         Start your repeating alarm here.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    Helpers helpers;
    @Override
    public void onReceive(Context context, Intent intent) {
        helpers = new Helpers();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            int interval = 8000;

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
            System.out.println("Alarm is fucking set");
            Intent resultIntent = new Intent(context, MainActivity.class);
            helpers.showNotification(context, resultIntent, 001, "Alarm!!!!", "ALARMA!!!", false);
        }
    }
}
