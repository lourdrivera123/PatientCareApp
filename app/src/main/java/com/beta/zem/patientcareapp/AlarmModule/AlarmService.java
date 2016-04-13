package com.beta.zem.patientcareapp.AlarmModule;

/**
 * Created by Dexter B. on 7/20/2015.
 */

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.PendingIntent;

import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.PatientConsultationController;
import com.beta.zem.patientcareapp.Controllers.PatientController;

public class AlarmService {
    Context context;
    DbHelper dbHelper;
    ArrayList<HashMap<String, String>> listOfAllConsultations;


    public AlarmService(Context context){
        this.context = context;
    }

    public void scheduleAlarm(ArrayList<HashMap<String, String>> arrayList, String alarmType)
    {
        try {
            if (alarmType.equals("consultations")) {
                // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
                // we fetch  the current time in milliseconds and added 1 day time
                // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
                //        GregorianCalendar calendar = new GregorianCalendar(2015, 6, 25, 11, 58, 0);
                //Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                GregorianCalendar calendar = new GregorianCalendar();
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                int y = 0;
                for (HashMap<String, String> map : arrayList) {
                    System.out.println("FUCKING CONSULTATION: " + map.toString());
                    y++;
                    if (map.get("isAlarm").equals("1") && map.get("finished").equals("0")) {
                        String time = map.get("alarmedTime").replace(" ", "");
                        System.out.println("FUCK: " + map.get("date").trim() + " " + (time.substring(0, time.length() - 2)) +
                                " " + time.substring(time.length() - 2, time.length()));

                        Date newDate = sdf.parse(map.get("date").trim() + " " + (time.substring(0, time.length() - 2)) +
                                " " + time.substring(time.length() - 2, time.length()));


                        calendar.setTime(newDate);

                        System.out.println("FUCKING NEW DATE: " + newDate.toString());

                        Long time2 = calendar.getTimeInMillis();
                        int id = Integer.parseInt(map.get("id"));


                        // create an Intent and set the class which will execute when Alarm triggers, here we have
                        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
                        // alarm triggers and
                        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
                        Intent intentAlarm = new Intent(context, AlarmReceiver.class);

                        // Bundle extras = new Bundle();
                        // extras.putSerializable("map", map);
                        intentAlarm.putExtra("id", map.get("id"));
                        intentAlarm.putExtra("doctor", map.get("doctor"));
                        intentAlarm.putExtra("clinic", map.get("clinic"));
                        intentAlarm.putExtra("partOfDay", map.get("partOfDay"));

                        // create the object

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intentAlarm, 0);

                        System.out.println("EXTRA ID: " + id);
                        //set the alarm for particular time2
                        // Integer.parseInt(alarmIntent.getStringExtra("id")), PendingIntent.FLAG_UPDATE_CURRENT
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time2, pendingIntent);

                        String msg = "Alarm Scheduled for " + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + " " +
                                calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "." + calendar.get(Calendar.MILLISECOND);

                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        System.out.println(msg);
                    }
                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void patientConsultationReminder(){
        dbHelper = new DbHelper(this.context);
        PatientConsultationController pc = new PatientConsultationController(this.context);
        PatientController ptc = new PatientController(this.context);
        listOfAllConsultations = pc.getAllConsultationsByUserId(ptc.getCurrentLoggedInPatient().getServerID());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        try{

            // context variable contains your `Context`
            // AlarmManager mgrAlarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            // ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

            scheduleAlarm(listOfAllConsultations, "consultations");

            int y = 0;
            /*for(HashMap<String, String> consultation : listOfAllConsultations){
                System.out.println("FUCKING CONSULTATION: "+consultation.toString());
                y++;
                if( consultation.get("isAlarm").equals("1") && consultation.get("finished").equals("0") ){
                    String time = consultation.get("alarmedTime").replace(" ", "");
                    System.out.println("FUCK: "+consultation.get("date").trim() + " " + (time.substring(0, time.length()-2))+
                            " "+time.substring(time.length()-2, time.length()) );

                    Date newDate = sdf.parse(consultation.get("date").trim() + " " + (time.substring(0, time.length()-2))+
                            " "+time.substring(time.length()-2, time.length()));

                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(newDate);

                    scheduleAlarm(calendar, consultation);

                    System.out.println("FUCKING NEW DATE: "+newDate.toString());
                }
            }*/
        }catch(Exception e){
            System.out.println("Oh snap! We got some error <source: HomeTileActivityClone.java@onCreate>"+e.toString());
        }
    }

}