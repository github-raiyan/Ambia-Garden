package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AppEnded extends AppCompatActivity {
    TextView message;
    @Override
    public void onBackPressed() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_ended);
        message=findViewById(R.id.txt_message);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            message.setText(bundle.getString("message"));
        }
    }
}