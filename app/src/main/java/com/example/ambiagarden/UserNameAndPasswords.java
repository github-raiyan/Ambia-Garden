package com.example.ambiagarden;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Vector;

public class UserNameAndPasswords extends AsyncTask<Void,Void,Void> {
    public JSONObject response;
    Vector<Record>loginList=new Vector<>();
    public String username,password;
    public String position;
    String sheetID="1j09V-VISn1Xg2Tx4FatsB3wRO6e88HkjWiHoK9kj-74";
    String link;
    Context context;
    UserNameAndPasswords(Context context,String username, String password){
        this.link="https://spreadsheets.google.com/feeds/list/"+sheetID+"/3/public/values?alt=json";
        this.context=context;
        this.username=username;
        this.password=password;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(link);
        if(jsonStr!=null){
            try {
                response = new JSONObject(jsonStr);
                makeList(response);
            }
            catch (Exception e){

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //printRecords();


    }
    private void makeList(JSONObject object){
        try {
            object=object.getJSONObject("feed");
            JSONArray jsonArray=object.getJSONArray("entry");
            for(int i=1;i<jsonArray.length();i++){
                object=jsonArray.getJSONObject(i);
                object=object.getJSONObject("content");
                String str=object.getString("$t");
                str=modifyString(str);
                object=new JSONObject(str);

                try{
                    Record record=new Record();
                    record.userName=parseRecord(object,"b");
                    record.password=parseRecord(object,"c");
                    record.position=parseRecord(object,"d");
                    loginList.add(record);

                }catch (Exception ee){
                    break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    String parseRecord( JSONObject object,String key){
        try{
            String str=object.getString(key);
            return str;
        }catch (Exception e){

        }
        return null;
    }

    private String addChar(String str,int position){
        return str.substring(0,position)+"\""+str.substring(position);
    }
    private String modifyString(String str){

        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==':'){
                str=addChar(str,i+1);
                str=addChar(str,i);
                str=addChar(str,i-1);
                i+=6;
            }
        }
        str+="\"";
        str="{"+str+"}";
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==','){
                str=addChar(str,i);
                i+=1;
            }
        }
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)=='"'&&str.charAt(i+1)==' '){
                str=str.substring(0,i+1)+str.substring(i+2);
            }
        }
        return str;
    }
    public int verifyUser(){//return 1 for success, return 0 for wrong pass, return -1 for wrong user.

        for (int i=0;i<loginList.size();i++){
            if(username.equals(loginList.get(i).userName)){
                if(password.equals(loginList.get(i).password)){
                    position=loginList.get(i).position;
                    JsonFile newJson=new JsonFile(context);
                    newJson.setUserJson(username,password,position,false);
                    return 1;
                }
                else {
                    return 0;
                }

            }
        }
        //System.out.println("username----------->>>>>>"+username);
        //printRecords();
        return -1;
    }
    void printRecords(){

        System.out.println("user names and passwords--------->>>>>>>>>>>");
        for(int i=0;i<loginList.size();i++){
            System.out.println("username: "+loginList.get(i).userName+" pass: "+loginList.get(i).password+" position: "+loginList.get(i).position);
        }
    }
    class Record{
        String userName,password,position;
    }
}
