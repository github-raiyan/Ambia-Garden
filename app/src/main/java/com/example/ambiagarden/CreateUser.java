package com.example.ambiagarden;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CreateUser extends AsyncTask<Void,Void,Void> {
    public JSONObject response;
    String updatedDate;
    Vector<Record>history=new Vector<>();
    Bill bill=new Bill();
    char position;
    Context context;
    String sheetID="1j09V-VISn1Xg2Tx4FatsB3wRO6e88HkjWiHoK9kj-74";
    String link;
    CreateUser(Context context,String workSheetID, char position){
        this.link="https://spreadsheets.google.com/feeds/list/"+sheetID+"/"+workSheetID+"/public/values?alt=json";
        this.position=position;
        this.context=context;
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
                printHistory();
                makeBill(response);

            }
            catch (Exception e){

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
    void prntBill(){
        System.out.println("bill------>>>>>>>>"+bill.currentReading+"    ------->>>>>>>"+bill.remainingAmount);
    }
    private void makeList(JSONObject object){
        try {
            object=object.getJSONObject("feed");
            JSONArray jsonArray=object.getJSONArray("entry");

            for(int i=13;i<jsonArray.length();i++){
                object=jsonArray.getJSONObject(i);
                object=object.getJSONObject("content");
                String str=object.getString("$t");
                str=modifyString(str);
                object=new JSONObject(str);

                try {
                    String key=""+position;
                    key=key.toLowerCase();
                    Record record=new Record();
                    record.date=object.getString(key);
                    key=""+(char)(position+1);
                    key=key.toLowerCase();
                    record.reading=object.getString(key);
                    key=""+(char)(position+2);
                    key=key.toLowerCase();
                    record.amount=object.getString(key);
                    history.add(record);
                    //System.out.println("record------>>>>"+record);
                }catch (Exception e){

                    try {
                        //Record record =new Record();
                        //record.date=object.getString(""+position);
                        String key=""+position;
                        key=key.toLowerCase();
                        updatedDate=object.getString(key);
                        //record.reading=object.getString(""+(char)(position+1));
                        //record.amount="---";
                        //history.add(record);
                    }catch (Exception ee){ }


                    break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.reverse(history);
    }
    String parseRecord( JSONObject object,String key){

        try {
            object=object.getJSONObject("content");
            String str=object.getString("$t");
            str=modifyString(str);
            object=new JSONObject(str);
            if(!object.has(key)){
                return null;
            }
            return object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return null;
    }
    private void makeBill(JSONObject object){
        try{
            object=object.getJSONObject("feed");
            JSONArray jsonArray=object.getJSONArray("entry");
            Character c=(char)(position+1);
            String key=""+(char)(position+1);
            key=key.toLowerCase();
            bill.meterNo=parseRecord(jsonArray.getJSONObject(1),key);

            bill.perUnit=parseRecord(jsonArray.getJSONObject(2),key);
            bill.currentReading=parseRecord(jsonArray.getJSONObject(3),key);
            bill.prevReading=parseRecord(jsonArray.getJSONObject(4),key);
            bill.lastRecharge=parseRecord(jsonArray.getJSONObject(5),key);
            bill.broughtUnit=parseRecord(jsonArray.getJSONObject(6),key);
            bill.usedUnit=parseRecord(jsonArray.getJSONObject(7),key);
            bill.remainingUnit=parseRecord(jsonArray.getJSONObject(8),key);
            bill.remainingAmount=parseRecord(jsonArray.getJSONObject(9),key);
            bill.canUseUntil=parseRecord(jsonArray.getJSONObject(10),key);
            bill.avgMonthly=parseRecord(jsonArray.getJSONObject(11),key);
            bill.updatedDate=updatedDate;


        }catch (Exception e){

        }
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
    void printHistory(){
        System.out.println("History list--------->>>>>>>>>>>>");
        for(int i=0;i<history.size();i++){
            System.out.println("Date: "+history.get(i).date+" reading: "+history.get(i).reading+" amount: "+history.get(i).amount);
        }
    }
}
