package com.example.ambiagarden;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileFragment extends Fragment {
    TextView usertxt,passwordtxt,expDatetxt;
    Button logoutBtn;
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
        update();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonFile jsonFile=new JsonFile(getContext());
                jsonFile.del();
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
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
            expDatetxt.setText("Automatically logout on: "+expString);

        }catch (Exception e){

        }
    }

}