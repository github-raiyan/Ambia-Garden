package com.example.ambiagarden;

import android.app.job.JobScheduler;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    TextView usertxt,passwordtxt,expDatetxt;
    Button logoutBtn;
    ImageButton btnCopy;
    ImageButton mssgBtn;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        usertxt=view.findViewById(R.id.txt_usename);
        passwordtxt=view.findViewById(R.id.txt_password);
        expDatetxt=view.findViewById(R.id.txt_expDate);
        logoutBtn=view.findViewById(R.id.btn_logout);
        btnCopy=view.findViewById(R.id.img_copy);
        mssgBtn=view.findViewById(R.id.msg_flowtBtn);
        update();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler scheduler=(JobScheduler)getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);

                JsonFile jsonFile=new JsonFile(getContext());
                jsonFile.del();
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                jsonFile.del();
                startActivity(intent);
                getActivity().finish();
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("01571219418","01571219418");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Number copied",Toast.LENGTH_SHORT).show();
            }
        });
        mssgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), UserMssg.class);
                startActivity(intent);
            }
        });
        return view;
    }
    void update(){
        JsonFile jsonFile=new JsonFile(getContext());
        JSONObject object=jsonFile.getUserJson();
        try {
            usertxt.setText("User name: "+object.getString("userName"));
            passwordtxt.setText("Password: "+object.getString("password"));

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat newFormat=new SimpleDateFormat("dd MMMM yyyy");
            String expString=object.getString("expiryDate");
            expString=newFormat.format(simpleDateFormat.parse(expString));
            expDatetxt.setText("You will automatically logout\non: "+expString);

        }catch (Exception e){

        }
    }

}