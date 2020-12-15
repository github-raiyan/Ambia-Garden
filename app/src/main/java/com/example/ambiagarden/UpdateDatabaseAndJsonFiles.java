package com.example.ambiagarden;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;

public class UpdateDatabaseAndJsonFiles {
    Context context;
    String workSheetID,index;
    public UpdateDatabaseAndJsonFiles(Context context) {
        this.context = context;

    }
    public void update() {
        if(!MainActivity.isInternetAvailable()){
            Toast.makeText(context,"No internet",Toast.LENGTH_SHORT).show();
        }
        JsonFile jsonFile=new JsonFile(context);
        if(jsonFile.getBillJson()==null||jsonFile.getUserJson()==null){
            return;
        }
        try {
            index=jsonFile.getUserJson().getString("position");
            if(index.charAt(0)>='A'&&index.charAt(0)<='Z'){
                workSheetID="1";
            }
            else{
                workSheetID="2";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        class ChildOfCreateUser extends CreateUser {

            ChildOfCreateUser(Context context, String workSheetID, char position) {
                super(context, workSheetID, position);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jsonFile.setBillJson(bill);
                DatabaseHelper databaseHelper=new DatabaseHelper(context);
                if(databaseHelper.insertData(history)){
                    Toast.makeText(context,"Bill updated",Toast.LENGTH_LONG);
                }
            }
        }
        ChildOfCreateUser childOfCreateUser =new ChildOfCreateUser(context,workSheetID,index.charAt(0));
        childOfCreateUser.execute();

    }

}
