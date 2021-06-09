package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT=1;
    TabLayout tabLayout;
    ViewPager viewPager;
    boolean internetAvailable=false;
    private final int[] tabIcons={
            R.drawable.home,
            R.drawable.history,
            R.drawable.calculator,
            R.drawable.profile
    };
    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        internetAvailable=false;
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        internetAvailable=false;
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }*/
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        checkStatus();
        JsonFile jsonFile=new JsonFile(getApplicationContext());
        if(jsonFile.getUserName()!=null&&jsonFile.getUserName().equals("admin")){
            JsonFile jsonFile1=new JsonFile(getApplicationContext());
            try {
                if(jsonFile.getUserJson().getBoolean("login")){
                    Intent intent=new Intent(MainActivity.this,AdminPanel.class);
                    startActivity(intent);

                    finish();
                }
                else{
                    callLoginActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            viewPager=findViewById(R.id.viewPager);
            setupViewPager(viewPager);
            tabLayout=findViewById(R.id.tablayout);
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();
            startServices();
        }

    }
    private void setupViewPager(ViewPager viewPager){
        FragmentAdapter adapter=new FragmentAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new HistoryFragment());
        adapter.addFragment(new CalculatorFragment());
        adapter.addFragment(new ProfileFragment());
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }
    void checkStatus(){

        JsonFile jsonFile=new JsonFile(getApplicationContext());
        String username,pass;
        if(jsonFile.getBillJson()==null||jsonFile.getUserJson()==null||!jsonFile.isValidUser()){//checking login status and exp
            callLoginActivity();
            return;
        }
        try{
            username=jsonFile.getUserJson().getString("userName");
            pass=jsonFile.getUserJson().getString("password");

        }catch (Exception e){
            callLoginActivity();
            return;
        }
        class PasswordList extends UserNameAndPasswords{

            PasswordList(Context context, String username, String password) {
                super(context, username, password);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(Check.isNetworkAvailable(getApplicationContext())){
                    internetAvailable=true;
                }
                else{
                    internetAvailable=false;
                }

                if(internetAvailable&&verifyUser()!=1){

                    jsonFile.del();
                    callLoginActivity();
                    //finish();
                }

                else if(internetAvailable){//update database

                    class ChildOfCreateUser extends CreateUser{

                        ChildOfCreateUser(Context context, String workSheetID, char position) {
                            super(context, workSheetID, position);
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if(!jsonFile.getUserName().equals("admin")){
                                jsonFile.setBillJson(bill);
                                DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
                                if(databaseHelper.insertData(history)){
                                    Toast.makeText(getApplicationContext(),"Bill updated",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    //Toast.makeText(getApplicationContext(),"Bill is up to date",Toast.LENGTH_SHORT).show();
                                }

                            }

                            updateUserInfo();
                        }
                    }
                    String worksheetID;
                    if(position==null){
                        return;
                    }
                    if(position.charAt(0)>='A'&&position.charAt(0)<='Z'){
                        worksheetID="1";
                    }
                    else{
                        worksheetID="2";
                    }
                    ChildOfCreateUser childOfCreateUser=new ChildOfCreateUser(getApplicationContext(),worksheetID,position.charAt(0));
                    childOfCreateUser.execute();
                }
            }
        }
        PasswordList passwordList=new PasswordList(getApplicationContext(),username,pass);

        passwordList.execute();
    }
    void callLoginActivity(){

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    void updateUserInfo(){

        UpdateUserInformation updateUserInformation=new UpdateUserInformation(this);
        updateUserInformation.execute();
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
    void sleep(){
        try{
            Thread.sleep(3000);
        }
        catch (Exception e){

        }
    }
}