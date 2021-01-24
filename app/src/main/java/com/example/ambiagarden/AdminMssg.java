package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class AdminMssg extends AppCompatActivity {
    TextView msgRayhan,msgUser,txtSeen,txtDelivered,txtUser;
    EditText typeMsg;
    ImageButton sendBtn;
    ImageView imgUser,backBtn;
    String userName;

    // ei activity ta userMssg theke copy kora. so ekhane user name "rayhan" ar rayhan mane "user"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mssg);
        userName=getIntent().getStringExtra("userName");

        msgRayhan=findViewById(R.id.msg_rayhan_admin);
        msgUser=findViewById(R.id.msg_usr_admin);
        typeMsg=findViewById(R.id.edt_msg_admin);
        sendBtn=findViewById(R.id.btn_send_admin);
        imgUser=findViewById(R.id.img_usr_admin);
        backBtn=findViewById(R.id.back_btn_admin);
        txtSeen=findViewById(R.id.txt_seen_admin);
        txtDelivered=findViewById(R.id.txt_delivered_admin);
        txtUser=findViewById(R.id.txt_inboxName_admin);
        txtUser.setText(userName);
        getMssg();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(false&&!Check.isNetworkAvailable(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"No internet.",Toast.LENGTH_LONG).show();
                    return;
                }
                String mssg=typeMsg.getText().toString();
                String temp=mssg.replace(" ","");
                if(temp.isEmpty()){
                    return;
                }
                imgUser.setVisibility(View.VISIBLE);
                msgUser.setVisibility(View.VISIBLE);
                msgUser.setText(mssg);
                sendtoFirebase(mssg);
                typeMsg.setText("");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void getMssg(){

        JsonFile jsonFile=new JsonFile(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(userName).child("personalMessage");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null&&!value.isEmpty()&&value.charAt(0)=='.'){
                    msgRayhan.setText(value);
                    msgUser.setVisibility(View.INVISIBLE);
                    imgUser.setVisibility(View.INVISIBLE);
                }
                else if(value!=null&&!value.isEmpty()&&value.charAt(0)!='.'){
                    msgUser.setText(value);
                    msgUser.setVisibility(View.VISIBLE);
                    imgUser.setVisibility(View.VISIBLE);
                }
                //System.out.println("Value--->>>>>"+value);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                //System.out.println("calling Failed->>>>>>>>>>>>>>>");
            }
        });
        DatabaseReference seen=database.getReference(userName).child("seen");
        DatabaseReference delivery=database.getReference(userName).child("delivery");
        seen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null&&value.equals("yes")){
                    txtSeen.setText("seen");
                }else {
                    txtSeen.setText("");
                }
                //System.out.println("Value--->>>>>"+value);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                //System.out.println("calling Failed->>>>>>>>>>>>>>>");
            }
        });
        delivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null&&value.equals("yes")){
                    txtDelivered.setText("delivered");
                }else{
                    txtDelivered.setText("");
                }
                //System.out.println("Value--->>>>>"+value);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                //System.out.println("calling Failed->>>>>>>>>>>>>>>");
            }
        });


    }
    void sendtoFirebase(String mssg){
        // Write a message to the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(userName);
        myRef.child("personalMessage").setValue(mssg);
        myRef.child("delivery").setValue("no");
        myRef.child("seen").setValue("no");
        database.getReference("lastMessage").setValue("rayhan");
    }
}