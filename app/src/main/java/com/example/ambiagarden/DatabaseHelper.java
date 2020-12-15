package com.example.ambiagarden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Vector;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="ambiaGarden.db";
    private static final String TABLE_NAME="history";
    private static final int VERSION_NUMBER=1;
    private static final String dates="Date",Reading="Reading",Amount="Amount";
    private static final String GET_FIRST_ELEMENT="SELECT * FROM "+TABLE_NAME+" ORDER BY Reading DESC LIMIT 1;";
    private static final String GET_ALL_ELEMENTS ="SELECT * FROM "+TABLE_NAME+" ORDER BY Reading DESC;";
    private static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("+dates+" VARCHAR(30) PRIMARY KEY,"+Reading+" INTEGER,"+Amount+" VARCHAR(30));";
    private static final String DROP_TABLe="DROP TABLE IF EXISTS "+TABLE_NAME;
    Context context;
    Vector<Record>history=new Vector<>();
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Toast.makeText(context,"Database created",Toast.LENGTH_LONG).show();
            //System.out.println("on create is called");

        }catch (Exception e){
            Toast.makeText(context,"Expection: "+e,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(DROP_TABLe);
            //Toast.makeText(context,"on upgrade is called",Toast.LENGTH_LONG).show();
            //System.out.println("on upgrade is called");
            onCreate(db);
        }catch (Exception e){
            Toast.makeText(context,"Expection: "+e,Toast.LENGTH_LONG).show();
        }

    }
    public boolean insertData(Vector<Record> latestList){

        String oldDate=getTheFirstRow();
        String newDate="";
        if(!latestList.isEmpty()){
            newDate=latestList.get(0).date;
        }
        history=latestList;
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);

        for(int i=0;i<history.size();i++){
            Record record=new Record();
            record.date=history.get(i).date;
            record.reading=history.get(i).reading;
            record.amount=history.get(i).amount;
            try{
                if(insertRecord(record)==-1){

                    updateRecord(record);
                }
            }catch (Exception e){

                updateRecord(record);
            }

        }
        System.out.println("older->>>>>>"+oldDate+"  -----newer->>>>>"+newDate);

        if(oldDate.equals(newDate)){

            return false;
        }
        else{
            return true;
        }
    }
    private long insertRecord(Record record){

        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(dates,record.date);
        contentValues.put(Reading,Integer.parseInt(record.reading));
        contentValues.put(Amount,record.amount);
        return sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
    }
    private void updateRecord(Record record){
        System.out.println("updating------>>>>>>"+record.date);
        SQLiteDatabase sqLiteDatabase =this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(dates,record.date);
        contentValues.put(Reading,Integer.parseInt(record.reading));
        contentValues.put(Amount,record.amount);
        sqLiteDatabase.update(TABLE_NAME,contentValues,"Date = ?",new String[]{record.date});
    }
    private String getTheFirstRow(){
        SQLiteDatabase sqLiteDatabase =this.getWritableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery(GET_FIRST_ELEMENT,null);
        String str="";
        if(cursor.moveToFirst()){
            str=cursor.getString(0);
        }
        cursor.close();
        return str;
    }
    public void readData(Vector<Record>latestList){
        latestList.clear();
        SQLiteDatabase sqLiteDatabase =this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(GET_ALL_ELEMENTS,null);
        if(cursor.getCount()==0){
            return ;
        }
        StringBuffer stringBuffer=new StringBuffer();
        while (cursor.moveToNext()){
            Record record=new Record();
            record.date=cursor.getString(0);
            record.reading=cursor.getString(1);
            record.amount=cursor.getString(2);
            //System.out.println("record---->>>date---->>>"+record.date+" reading--->>>"+record.reading);
            if(record==null)continue;
            latestList.add(record);
        }
        //System.out.println("datebase---->>>>>>");
        //pList(latestList);
    }
    void pList(Vector<Record>v){
        for(int i=0;i<v.size();i++){
            System.out.println("date-->>"+v.get(i).date+"  Reading-->>"+v.get(i).reading+" amount--->>>"+v.get(i).amount);
        }
    }
}
