package com.example.ambiagarden;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JsonFile {
    JSONObject userJson=null;
    JSONObject billJson=null;
    JSONObject preUserJson=null;
    Context context;

    JsonFile(Context context){
        this.context=context;
        userJsonFileReader();
        billJsonFileReader();
    }

    public JSONObject getUserJson(){
        return userJson;
    }
    public JSONObject getBillJson(){
        return billJson;
    }
    public void del(){
        try {
            //userJson=new JSONObject("{}");
            //billJson=new JSONObject("{}");
            userJson.put("login",false);
            userJsonFileWriter();
            //billJsonFileWriter();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getUserName(){
        String uName=null;
        if(userJson==null)
            return uName;
        try {
            uName=userJson.getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uName;
    }
    public void setUserJson(String username,String password,String position,boolean updateExpirayDate){
        try {
            userJson=new JSONObject("{}");
            if(username!=null)
                userJson.put("userName", username);
            if(password!=null)
                userJson.put("password", password);
            if(position!=null)
                userJson.put("position", position);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            Calendar calendar= Calendar.getInstance();
            try{
                calendar.setTime(new Date());
            }catch (Exception e){
                e.printStackTrace();
            }
            calendar.add(Calendar.DAY_OF_MONTH,366);
            String ExpDate=simpleDateFormat.format(calendar.getTime());
            if( !updateExpirayDate){
                JsonFile extraJsonForGetDate=new JsonFile(context);
                if(extraJsonForGetDate.getUserJson()!=null)
                    ExpDate=extraJsonForGetDate.getUserJson().getString("expiryDate");
            }
            System.out.println("updating exp---->>>>>>>>>>");
            userJson.put("expiryDate",ExpDate);
            userJson.put("login",true);
            userJsonFileWriter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setBillJson(Bill bill){
        try {
            billJson =new JSONObject("{}");
            billJson.put("updatedDate",bill.updatedDate);
            billJson.put("meterNo",bill.meterNo);
            billJson.put("perUnit",bill.perUnit);
            billJson.put("currReading",bill.currentReading);
            billJson.put("prevReading",bill.prevReading);
            billJson.put("lastRecharge",bill.lastRecharge);
            billJson.put("broughtUnit",bill.broughtUnit);
            billJson.put("usedUnit",bill.usedUnit);
            billJson.put("remainingUnit",bill.remainingUnit);
            billJson.put("remainingAmount",bill.remainingAmount);
            billJson.put("canUseUntil",bill.canUseUntil);
            billJson.put("avgMonthly",bill.avgMonthly);

            billJsonFileWriter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void userJsonFileReader(){
        String data="";
        File file= new File(context.getFilesDir(),"userJson.txt");
        if(!file.exists()){
            System.out.println("User File not exist---->>>>>");
            return;
        }
        try {
            FileInputStream fileInputStream = context.openFileInput("userJson.txt");
            InputStreamReader inputStreamReader= new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null){
                data+=line;
            }
            userJson=new JSONObject(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void billJsonFileReader(){
        String data="";
        File file= new File(context.getFilesDir(),"billJson.txt");
        if(!file.exists()){
            System.out.println("BILL File not exist---->>>>>");
            return;
        }
        try {
            FileInputStream fileInputStream = context.openFileInput("billJson.txt");
            InputStreamReader inputStreamReader= new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null){
                data+=line;
            }
            billJson=new JSONObject(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void userJsonFileWriter(){
        try {
            FileOutputStream fileOutputStream= context.openFileOutput("userJson.txt", Context.MODE_PRIVATE);
            try {
                fileOutputStream.write(userJson.toString().getBytes());
                fileOutputStream.close();
                //Toast.makeText(context,"File saved",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    void billJsonFileWriter(){
        try {

            FileOutputStream fileOutputStream= context.openFileOutput("billJson.txt", Context.MODE_PRIVATE);
            try {
                fileOutputStream.write(billJson.toString().getBytes());
                fileOutputStream.close();
                //Toast.makeText(context,"File saved",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public boolean isValidUser(){
        try {
            boolean status=userJson.getBoolean("login");

            if(!status){

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            Calendar currDate= Calendar.getInstance();
            String expString=userJson.getString("expiryDate");
            Calendar expDate=Calendar.getInstance();
            try{
                currDate.setTime(new Date());
                expDate.setTime(simpleDateFormat.parse(expString));

                if(currDate.after(expDate)){
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return  false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
        return  true;
    }
}