package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    String[] userNamesString;
    Spinner spinner;
    ImageButton imgEye;
    String userName;
    String passWord;
    EditText editTextPassword;
    ProgressBar progressBar;
    TextView wrongUserName,wrongPassword;
    boolean hidden=true;
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JsonFile destroyingJson=new JsonFile(getApplicationContext());
        destroyingJson.del();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        userNamesString=getResources().getStringArray(R.array.usernames);
        editTextPassword=findViewById(R.id.edtxt_password);
        spinner=findViewById(R.id.spinner_username);
        wrongPassword=findViewById(R.id.wrongPassword);
        wrongUserName=findViewById(R.id.wrongUsername);
        progressBar=findViewById(R.id.progressbar_login);
        ArrayAdapter<String>adapter=new ArrayAdapter<>(this,R.layout.spinner_view,R.id.spinner_txtView,userNamesString);
        spinner.setAdapter(adapter);
        imgEye=findViewById(R.id.img_eye);

        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hidden){
                    editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    hidden=false;
                }
                else{
                    editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    hidden=true;
                }
            }
        });
    }

    public void loginButton(View view) {
        class UserInfo extends UserNameAndPasswords{

            UserInfo(Context context, String username, String password) {
                super(context, username, password);
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(view.VISIBLE);


            }

            @Override
            protected void onPostExecute(Void aVoid) {
                int returnValue=verifyUser();
                if(returnValue==1){
                    //save Jsonfile and goto main activity
                    JsonFile jsonFile=new JsonFile(getApplicationContext());
                    jsonFile.setUserJson(username,password,position);


                    class CreatingBillJson extends CreateUser{

                        CreatingBillJson(Context context, String workSheetID, char position) {
                            super(context, workSheetID, position);
                        }

                        @Override
                        protected void onPreExecute() {
                            progressBar.setVisibility(view.VISIBLE);
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            jsonFile.setBillJson(bill);
                            progressBar.setVisibility(view.GONE);
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    char pss=position.charAt(0);
                    String workSheetId;
                    if(Character.isUpperCase(pss)){
                        workSheetId="1";
                    }
                    else{
                        workSheetId="2";
                    }
                    CreatingBillJson creatingBillJson=new CreatingBillJson(getApplicationContext(),workSheetId,pss);
                    creatingBillJson.execute();


                    //System.out.println("possition is-------->>>>>>>"+position);
                }
                else if (returnValue==0){

                    Toast.makeText(context,"Wrong Password",Toast.LENGTH_LONG).show();

                    //System.out.println("wrong password-------->>>>>>>>>>");
                }
                else{

                    Toast.makeText(context,"Invalid user name",Toast.LENGTH_LONG).show();

                    //System.out.println("wrong username-------->>>>>>>>>>");
                }
                progressBar.setVisibility(view.INVISIBLE);
            }
        }
        userName=spinner.getSelectedItem().toString();
        passWord=editTextPassword.getText().toString();
        UserInfo userInfo=new UserInfo(getApplicationContext(),userName,passWord);
        userInfo.execute();

    }
    void sleep(){
        try{
            Thread.sleep(1000);
        }
        catch (Exception e){

        }
    }

}