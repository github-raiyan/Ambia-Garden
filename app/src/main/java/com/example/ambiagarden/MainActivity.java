package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT=1;
    TabLayout tabLayout;
    ViewPager viewPager;
    private int[] tabIcons={
            R.drawable.home,
            R.drawable.history,
            R.drawable.calculator,
            R.drawable.profile
    };
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStatus();


        viewPager=findViewById(R.id.viewPager);
        setupViewPager(viewPager);


        tabLayout=findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about,menu);
        return true;
    }

    public void about(MenuItem item) {

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
            username=jsonFile.userJson.getString("userName");
            pass=jsonFile.userJson.getString("password");

        }catch (Exception e){
            callLoginActivity();
            return;
        }


        class PasswordList extends UserNameAndPasswords{

            PasswordList(Context context, String username, String password) {
                super(context, username, password);
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(isInternetAvailable()&&verifyUser()!=1){
                    callLoginActivity();
                    finish();
                }
                else if(isInternetAvailable()){//update database
                    class ChildOfCreateUser extends CreateUser{

                        ChildOfCreateUser(Context context, String workSheetID, char position) {
                            super(context, workSheetID, position);
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            jsonFile.setBillJson(bill);
                            DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
                            if(databaseHelper.insertData(history)){
                                Toast.makeText(getApplicationContext(),"Bill updated",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //Toast.makeText(getApplicationContext(),"Bill is up to date",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    String worksheetID;
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
    }
    public static boolean isInternetAvailable() {

        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }


    }
    void sleep(){
        try{
            Thread.sleep(3000);
        }
        catch (Exception e){

        }
    }

}
