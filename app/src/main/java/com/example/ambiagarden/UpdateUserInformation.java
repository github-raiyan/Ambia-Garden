package com.example.ambiagarden;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UpdateUserInformation extends AsyncTask<Void,Void,Void>{
    String userName;
    Context context;
    public JSONObject response;
    String link="https://ifconfig.co/json";
    public UpdateUserInformation(Context context) {
        this.context=context;

        JsonFile jsonFile = new JsonFile(context);
        try {
            userName=jsonFile.getUserJson().getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(link);
        if(jsonStr!=null){
            try {
                response = new JSONObject(jsonStr);
            }
            catch (Exception e){
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        updateToFirebase();
        checkAppEnded();
        super.onPostExecute(aVoid);
    }

    void updateToFirebase(){
        String s=response.toString();
        String ip="";
        String device="";
        try {
            ip=response.getString("ip");
            ip=ip.replace('.','-');
            device=response.getJSONObject("user_agent").getString("comment");
            device=device.replace('.',' ');
            device=device.replace(';',' ');
            device=device.replace('/',' ');
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String strDate = dateFormat.format(date);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        System.out.println("database--->>>"+database);
        DatabaseReference myRef = database.getReference(userName);
        myRef.child(device).child("Time").setValue(strDate);
        myRef.child(device).child("json").setValue(s);

    }
    void checkAppEnded(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("appEnded");

        //myRef.setValue("Hello, World!");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null&&!value.isEmpty()&&value.charAt(0)!='.'){
                    callAppEndedActivity(value);
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
    void callAppEndedActivity(String message){
        Intent intent=new Intent(context,AppEnded.class);
        intent.putExtra("message",message);
        context.startActivity(intent);
    }
}