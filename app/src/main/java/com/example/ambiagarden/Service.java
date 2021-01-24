package com.example.ambiagarden;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;


public class Service extends JobService {
    JobParameters params;
    String Jusername;
    @Override
    public boolean onStartJob(JobParameters params) {
        this.params=params;

        JsonFile jsonFile=new JsonFile(getApplicationContext());
        if(jsonFile.getUserJson()==null){
            cancelJob();
            return false;
        }
        //Toast.makeText(getApplicationContext(),"Job started",Toast.LENGTH_LONG).show();
        if(!Check.isNetworkAvailable(getApplicationContext())){
            return false;
        }

        try {
            if(jsonFile.getUserJson()!=null)
                Jusername=jsonFile.getUserJson().getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonFile.getUserName()!=null&&jsonFile.getUserName().equals("admin")){
            try {
                if(jsonFile.getUserJson().getBoolean("login")){
                    checkMessageOnFirebase();
                    if(Jusername.equals("admin")){
                        checkAdminMessage();
                    }else{
                        checkPersonalMessageOnFirebase();
                    }

                }else{
                    cancelJob();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }else{
            boolean state=false;
            try {

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

            UpdateAll updateAll=new UpdateAll(getApplicationContext());
            updateAll.update();
            return true;
        }

    }
    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("Job canceled--------->>>>>>>>>");
        return false;
    }
    void  cancelJob(){
        JobScheduler scheduler=(JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }

    private class UpdateAll {
        String preBalance;
        Context context;
        String workSheetID,index;
        public UpdateAll(Context context) {
            this.context = context;
        }
        public void update() {
            JsonFile jsonFile=new JsonFile(context);
            if(jsonFile.getBillJson()==null||jsonFile.getUserJson()==null){
                return;
            }
            try {
                index=jsonFile.getUserJson().getString("position");
                if(index.charAt(0)>='A'&&index.charAt(0)<='Z'){
                    workSheetID="1";
                }
                else{
                    workSheetID="2";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            class ChildOfCreateUser extends CreateUser {

                ChildOfCreateUser(Context context, String workSheetID, char position) {
                    super(context, workSheetID, position);
                }
                @Override
                protected void onPreExecute() {
                    try{
                        checkMessageOnFirebase();
                        checkPersonalMessageOnFirebase();
                    }catch (Exception e){

                    }

                    try {
                        preBalance=jsonFile.getBillJson().getString("remainingAmount");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    jsonFile.setBillJson(bill);
                    DatabaseHelper databaseHelper=new DatabaseHelper(context);
                    if(databaseHelper.insertData(history) || !preBalance.equals(bill.remainingAmount)){
                        //Toast.makeText(context,"Bill updated",Toast.LENGTH_LONG).show();
                        showNotification(bill.remainingAmount);
                    }

                    jobFinished(params,false);
                }
            }
            ChildOfCreateUser childOfCreateUser =new ChildOfCreateUser(context,workSheetID,index.charAt(0));
            childOfCreateUser.execute();
        }
    }
    void showNotification(String balance){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("Ambia Garden","Ambia Garden", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"Ambia Garden");
        builder.setContentTitle("বিদ্যুৎ বিল আপডেট হয়েছে।");
        builder.setContentText("বর্তমান ব্যাল্যান্সঃ  "+balance+" টাকা");
        builder.setSmallIcon(R.drawable.notificationicon);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //notification.setSmallIcon(R.drawable.icon_transperent);
            builder.setColor(getResources().getColor(R.color.notification_color));
        } else {
            //notification.setSmallIcon(R.drawable.icon);
        }
        builder.setAutoCancel(true);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(123,builder.build());
    }
    void checkMessageOnFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("broadcastMessage");

        //myRef.setValue("Hello, World!");
        // Read from the database
             myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot==null)return;

                String value = dataSnapshot.getValue(String.class);

                if(value!=null&&!value.isEmpty()&&value.charAt(0)!='.'){
                    showMessage(value,000,false);
                }
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                System.out.println("Failed to read value."+error.toException());
            }
        });
    }
    void checkPersonalMessageOnFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef =database.getReference(Jusername);;

        //myRef.setValue("Hello, World!");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot==null)return;

                String value =dataSnapshot.child("personalMessage").getValue(String.class);
                JsonFile newJson=new JsonFile(getApplicationContext());
                if(!newJson.getUserName().equals("admin")&&value!=null&&!value.isEmpty()&&value.charAt(0)!='.'){
                    showMessage(value,001,true);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                System.out.println("Failed to read value."+error.toException());
            }
        });
    }
    void checkAdminMessage(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("lastMessage");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot==null)return;

                    String value =dataSnapshot.getValue(String.class);
                    JsonFile newJson=new JsonFile(getApplicationContext());
                    if(newJson.getUserName().equals("admin")&&value!=null&&!value.isEmpty()&&!value.equals("rayhan")){
                        //System.out.println("value-->>>>>>"+value);
                        showMessage(value,001,true);
                    }

                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                System.out.println("Failed to read value."+error.toException());
            }
        });
    }
    void showMessage(String message,int id,boolean isPersnalMessage){
        //System.out.println("mssg-->>>>>>>>>"+message);
        if(isPersnalMessage && !Jusername.equals("admin")){
            mssgStatustoFirebase("yes");
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("Ambia Garden","Ambia Garden", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        Bitmap largeIcon;
        if(isPersnalMessage&&Jusername.equals("admin")){
            largeIcon=BitmapFactory.decodeResource(getResources(),R.drawable.user);
        }
        else if(isPersnalMessage){
            largeIcon=BitmapFactory.decodeResource(getResources(),R.drawable.rayhan);
        }
        else {
            largeIcon=BitmapFactory.decodeResource(getResources(),R.drawable.appicon);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"Ambia Garden");
        builder.setContentTitle("Message");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.notificationicon);
        builder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(message)
            .setBigContentTitle("You have a message"))
            .setLargeIcon(largeIcon)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        //builder.addAction(R.drawable.rayhan,"ঠিক আছে।");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //notification.setSmallIcon(R.drawable.icon_transperent);
            builder.setColor(getResources().getColor(R.color.message_notification_color));
        } else {
            //notification.setSmallIcon(R.drawable.icon);
        }
        builder.setAutoCancel(true);
        if(!isPersnalMessage){
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        }
        else{
            PendingIntent thikAche = PendingIntent.getActivity(getApplicationContext(), 1,
                    new Intent(getApplicationContext(), UserMssg.class), PendingIntent.FLAG_UPDATE_CURRENT);
            if(Jusername.equals("admin")){
                thikAche = PendingIntent.getActivity(getApplicationContext(), 1,
                        new Intent(getApplicationContext(), AdminPanel.class), PendingIntent.FLAG_UPDATE_CURRENT);
            }
            builder.setContentIntent(thikAche);
            builder.addAction(R.drawable.eye,"ঠিক আছে।",thikAche).build();
        }

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(id,builder.build());
    }
    void mssgStatustoFirebase(String mssg){
        //mssg="..."+mssg;
        // Write a message to the database
        JsonFile jsonFile=new JsonFile(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(jsonFile.getUserName());
        myRef.child("delivery").setValue(mssg);
    }
}