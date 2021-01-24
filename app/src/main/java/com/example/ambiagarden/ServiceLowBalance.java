package com.example.ambiagarden;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;


public class ServiceLowBalance extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        //System.out.println("Job started--------->>>>>>>>>");
        //Toast.makeText(getApplicationContext(),"Lower balance started",Toast.LENGTH_LONG).show();
        JsonFile jsonFile=new JsonFile(getApplicationContext());
        if(jsonFile.getUserName()!=null&&jsonFile.getUserName().equals("admin")){
            cancelJob();
            return false;
        }
        boolean state=false;
        try {
            if(jsonFile.getUserJson()==null){
                cancelJob();
                return false;
            }
            state=jsonFile.getUserJson().getBoolean("login");
        } catch (JSONException e) {
            e.printStackTrace();
            cancelJob();
            return false;
        }
        if(!state){
            cancelJob();
            return false;
        }
        Double bill=0.0;
        try {
            bill=Double.parseDouble(jsonFile.getBillJson().getString("remainingAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(bill<=0.0){
            bill*=-1;
            showNotification("মিটারের ব্যাল্যান্স শেষ!!!","Loan: ",bill.toString(),"টাকা। দ্রুত রিচার্জ করুন। মিটার বন্ধ হয়ে যেতে পারে।");
        }
        else if(bill<=100.0){
            showNotification("মিটারের ব্যাল্যান্স শেষের দিকে।","বর্তমান ব্যাল্যান্সঃ  ",bill.toString(),"টাকা। রিচার্জ করে ফেলুন।");
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("Job canceled--------->>>>>>>>>");
        return false;
    }
    void showNotification(String tittle,String prefix,String balance,String waring){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("Ambia Garden","Ambia Garden", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"Ambia Garden");
        builder.setContentTitle(tittle);
        builder.setContentText(prefix+""+balance+" \n"+waring);
        builder.setSmallIcon(R.drawable.notificationicon);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //notification.setSmallIcon(R.drawable.icon_transperent);
            builder.setColor(getResources().getColor(R.color.low_notification_color));
        } else {
            //notification.setSmallIcon(R.drawable.icon);
        }
        builder.setAutoCancel(true);
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(prefix+""+balance+" \n"+waring)
                .setBigContentTitle(tittle));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(123,builder.build());

    }
    void  cancelJob(){
        JobScheduler scheduler=(JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(124);
    }
}