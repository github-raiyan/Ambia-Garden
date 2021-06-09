package com.example.ambiagarden;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Vector;

public class HomeFragment extends Fragment {
    final String[] widgetNames={"তারিখঃ ","মিটার নংঃ ","প্রতি ইউনিটঃ ","পূর্বের রিডিংঃ ","বর্তমান রিডিংঃ ",
            "বর্তমান ব্যাল্যান্সঃ ","গড় মাসিক ব্যয়ঃ ","ব্যাল্যান্স শেষ হবার সম্ভাব্যঃ  ","সর্বশেষ জমাঃ ","চালাতে পারবেঃ "};
    TextView txtDate,txtMeterNO,txtPerUnit,txtPreReading,txtCurrReading,
            txtCurrBalance,txtAvgBill,txtPossibleDate,txtLastRecharge,txtCanUse,loantxt,txtWaterMask;
    ImageButton update,download;
    ProgressBar progressBar;
    UpdateAll updateAll;
    Bitmap bitmap;
    ConstraintLayout llPdf;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        updateWidgets();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateAll=new UpdateAll(getContext());
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        findViews(view);
        updateWidgets();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAll.update();

                //updateWidgets();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission();
                txtWaterMask.setVisibility(View.VISIBLE);
                bitmap = loadBitmapFromView(llPdf, llPdf.getWidth(), llPdf.getHeight());
                createPdf();
                txtWaterMask.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
    private void askPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }
    private void createPdf(){
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;
        System.out.println("h="+hight+"  w="+width);
        int convertHighet = (int) hight, convertWidth = (int) width;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);
        // write the document content
        String targetPdf = getPath();//"/sdcard/pdffromlayout.pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            // close the document
            document.close();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Toast.makeText(getContext(), "PDF saved to Internal storage/Android/data/com.example.ambiagarden/files/Downloads/Ambia_Garden", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getContext(), "PDF saved to Internal storage/Downloads/Ambia Garden", Toast.LENGTH_LONG).show();
            }
            openGeneratedPDF();
            //openPDFviwer();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    private String getPath(){
        String path="";
        JsonFile jsonFile=new JsonFile(getContext());
        String currDate=null;
        try {
            currDate=jsonFile.getBillJson().getString("updatedDate");
            currDate=currDate.replace(" ","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path= getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator +"Ambia_Garden"+File.separator+ "eBill_" +currDate+".pdf";
            path=path.replace(" ","");
            File newDir = new File( getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator +"Ambia_Garden");
            if(!newDir.exists()){
                //System.out.println("creating dir-->>>>");
                newDir.mkdir();
            }else{
                //System.out.println("dir exists-->>>"+newDir);
            }
        }
        else{
            path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator +"Ambia_Garden"+File.separator+ "eBill_" +currDate+".pdf";
            path=path.replace(" ","");
            File newDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator +"Ambia_Garden");
            if(!newDir.exists()){
                System.out.println("creating dir-->>>>");
                newDir.mkdir();
            }
        }
        try {
            File file=new File(path);
            if(!file.exists()){
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                String bytes="";

                //fo.write(bytes.toByteArray());
                fo.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  path;
    }
    private void openGeneratedPDF(){
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        File file = new File(getPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),
                    "No Application Available to View PDF",
                    Toast.LENGTH_SHORT).show();
        }
    }
    void openPDFviwer(){
        //Intent intent=new Intent(getContext(), Pdfviewer.class);
        //startActivity(intent);
    }
    void findViews(View view){
        progressBar=view.findViewById(R.id.home_progressbar);
        txtDate=view.findViewById(R.id.lbl_date);//
        txtMeterNO=view.findViewById(R.id.lbl_meterNo);
        txtPerUnit=view.findViewById(R.id.lbl_perUnit);
        txtPreReading=view.findViewById(R.id.lbl_preReading);
        txtCurrReading=view.findViewById(R.id.lbl_currReading);
        txtCurrBalance=view.findViewById(R.id.lbl_currBalance);
        txtAvgBill=view.findViewById(R.id.lbl_avgBill);
        txtPossibleDate=view.findViewById(R.id.lbl_possibleDate);//
        txtLastRecharge=view.findViewById(R.id.lbl_lastRecharge);
        txtCanUse=view.findViewById(R.id.lbl_canUse);
        loantxt=view.findViewById(R.id.loantxt);
        update=view.findViewById(R.id.btn_update);
        download=view.findViewById(R.id.btn_download);
        llPdf=view.findViewById(R.id.llpdf);
        txtWaterMask=view.findViewById(R.id.txt_waterMask);
    }
    void updateWidgets(){
        JsonFile jsonFile=new JsonFile(getContext());
        JSONObject billJSON=jsonFile.getBillJson();
        if(jsonFile.getUserJson()==null||jsonFile.getBillJson()==null)
            return;
        try{
            txtDate.setText(widgetNames[0]+" "+billJSON.getString("updatedDate"));
            txtMeterNO.setText(widgetNames[1]+"   "+billJSON.getString("meterNo"));
            txtPerUnit.setText(widgetNames[2]+"   "+billJSON.getString("perUnit")+"   টাকা");
            //pre reading have to update from datebase due to google sheet complexity.
            txtPreReading.setText(widgetNames[3]+"   "+billJSON.getString("prevReading"));
            DatabaseHelper databaseHelper=new DatabaseHelper(getContext());
            Vector<Record>history=new Vector<>();
            databaseHelper.readData(history);
            if(billJSON.getString("prevReading").equals(billJSON.getString("currReading"))&&history.size()>0){
                txtPreReading.setText(widgetNames[3]+"   "+history.get(1).reading);
            }
            txtCurrReading.setText(widgetNames[4]+"   "+billJSON.getString("currReading"));
            txtCurrBalance.setText(widgetNames[5]+"   "+billJSON.getString("remainingAmount")+"  টাকা");
            txtAvgBill.setText(widgetNames[6]+"   "+billJSON.getString("avgMonthly")+"  টাকা");
            txtLastRecharge.setText(widgetNames[8]+"   "+billJSON.getString("lastRecharge")+"  টাকা");
            txtCanUse.setText(widgetNames[9]+""+billJSON.getString("canUseUntil")+"");
            //System.out.println("called ----->>>>>>>>>>>>>>update widgets");
            Double balance=0.0;
            Double avgBill=0.0;
            int days=0;
            try {
                balance=Double.parseDouble(billJSON.getString("remainingAmount"));
                avgBill=Double.parseDouble(billJSON.getString("avgMonthly"));

                 days= (int) ((30*balance)/avgBill);
                if(days<0)days=0;
            }catch (Exception e){

            }
            txtPossibleDate.setText(widgetNames[7]+days+"  দিন বাকি");
            if(avgBill==0){
                txtPossibleDate.setText(widgetNames[7]+"---"+"  দিন বাকি");
            }
            if(balance<0){
                loantxt.setText("(Loan)");
            }
            else{
                loantxt.setText("");
            }
        }catch (JSONException e) {
            e.printStackTrace();
            //System.out.println("failed ----->>>>>>>>>>>>>>update widgets------>>>"+e);
            //System.out.println("billJson ----->>>>>>>>>>>>>>"+billJSON);
        }
    }
    private class UpdateAll {
        Context context;
        String workSheetID,index;
        public UpdateAll(Context context) {
            this.context = context;
        }
        public void update() {
            if(!Check.isNetworkAvailable(getContext())){
                Toast.makeText(context,"No internet",Toast.LENGTH_SHORT).show();
                return;
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
                protected void onPreExecute() {
                    progressBar.setVisibility(View.VISIBLE);
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    jsonFile.setBillJson(bill);
                    DatabaseHelper databaseHelper=new DatabaseHelper(context);
                    if(databaseHelper.insertData(history)){
                        Toast.makeText(context,"Bill updated",Toast.LENGTH_LONG).show();
                    }
                    updateWidgets();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            ChildOfCreateUser childOfCreateUser =new ChildOfCreateUser(context,workSheetID,index.charAt(0));
            childOfCreateUser.execute();
        }
    }
}