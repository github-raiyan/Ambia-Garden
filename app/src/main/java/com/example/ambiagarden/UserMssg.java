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

public class UserMssg extends AppCompatActivity {
    TextView msgRayhan,msgUser;
    EditText typeMsg;
    ImageButton sendBtn;
    ImageView imgUser,backBtn;
    boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mssg);
        msgRayhan=findViewById(R.id.msg_rayhan);
        msgUser=findViewById(R.id.msg_usr);
        typeMsg=findViewById(R.id.edt_msg);
        sendBtn=findViewById(R.id.btn_send);
        imgUser=findViewById(R.id.img_usr);
        backBtn=findViewById(R.id.back_btn);

        UpdateUserInformation updateUserInformation=new UpdateUserInformation(this);
        updateUserInformation.execute();
        getMssg();
        mssgStatustoFirebase("yes");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Check.isNetworkAvailable(getApplicationContext())){
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
    @Override
    public void onStart() {
        super.onStart();
        active = true;
        mssgStatustoFirebase("yes");
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;

    }

    void getMssg(){

        JsonFile jsonFile=new JsonFile(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(jsonFile.getUserName()).child("personalMessage");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null&&!value.isEmpty()&&value.charAt(0)!='.'){
                    msgRayhan.setText(value);
                    msgUser.setVisibility(View.INVISIBLE);
                    imgUser.setVisibility(View.INVISIBLE);
                    if(active){
                        mssgStatustoFirebase("yes");
                    }
                }
                else if(value!=null&&!value.isEmpty()&&value.charAt(0)=='.'&&!value.equals(".")){
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
            }
        });

    }
    void sendtoFirebase(String mssg){
        // Write a message to the database
        JsonFile jsonFile=new JsonFile(this);
        mssg="."+mssg;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(jsonFile.getUserName());
        myRef.child("personalMessage").setValue(mssg);
        database.getReference("lastMessage").setValue(jsonFile.getUserName()+": "+mssg);
    }
    void mssgStatustoFirebase(String mssg){
        //mssg="..."+mssg;
        // Write a message to the database
        JsonFile jsonFile=new JsonFile(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(jsonFile.getUserName());
        myRef.child("seen").setValue(mssg);
    }
}