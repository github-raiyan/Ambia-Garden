package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class AdminPanel extends AppCompatActivity {
    TextView broadcastMssg;
    EditText broadcastEdt;
    AppCompatButton btnLogout;
    ImageButton broadcastSend;
    ListView listMssg;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        broadcastMssg=findViewById(R.id.txt_mssBroadcast);
        broadcastEdt=findViewById(R.id.edt_broadcast);
        broadcastSend=findViewById(R.id.btn_sendBroadcast);
        listMssg=findViewById(R.id.lst_mssg);
        btnLogout=findViewById(R.id.btn_adminLogout);
        String[] names =getResources().getStringArray(R.array.usernames);
        names= Arrays.copyOfRange(names, 1, names.length-1);

        ArrayAdapter<String>adapter=new ArrayAdapter<String>(AdminPanel.this,R.layout.list_mssg,R.id.txt_oneIndex,names);
        listMssg.setAdapter(adapter);
        getBroadcast();
        startServices();
        broadcastMssg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=broadcastEdt.getText().toString()+broadcastMssg.getText();
                broadcastEdt.setText(str);
            }
        });
        broadcastSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(broadcastEdt.getText().toString().isEmpty()){
                   return;
                }
                setBroadcastMssg();
                broadcastEdt.setText("");
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonFile jsonFile=new JsonFile(getApplicationContext());
                jsonFile.del();
                Intent intent=new Intent(AdminPanel.this,MainActivity.class);
                jsonFile.del();
                startActivity(intent);
                finish();
            }
        });
    }
    void getBroadcast(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("broadcastMessage");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                //System.out.println("get Broadcast--->>>>"+value);
                broadcastMssg.setText(value);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                broadcastMssg.setText("offline");
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        listMssg.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Intent intent=new Intent(AdminPanel.this,AdminMssg.class);
                intent.putExtra("userName",value);
                startActivity(intent);

            }
        });
    }
    void setBroadcastMssg(){
        String mssg=broadcastEdt.getText().toString();
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("broadcastMessage");
        myRef.setValue(mssg);
    }
    void startServices(){
        ComponentName componentName=new ComponentName(this,Service.class);
        JobInfo jobInfo=new JobInfo.Builder(123,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(7200007)//7200007
                .setPersisted(true)
                .build();
        JobScheduler scheduler= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
        //low balance service
        ComponentName componentName2=new ComponentName(this,ServiceLowBalance.class);
        JobInfo jobInfo2=new JobInfo.Builder(124,componentName2)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setMinimumLatency(64*1000)
                .setPeriodic(43200007)//43200007
                .setPersisted(true)
                .build();
        JobScheduler scheduler2= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler2.schedule(jobInfo2);
    }

}