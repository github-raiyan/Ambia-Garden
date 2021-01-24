package com.example.ambiagarden;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Pdfviewer extends AppCompatActivity {
    PDFView pdfView;
    TextView txtPath;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        File file = new File(getPath());
        pdfView=findViewById(R.id.pdfView);
        txtPath=findViewById(R.id.txtPath);
        backBtn=findViewById(R.id.back_btnPDF);
        String path="PDF location: Internal storage-->"+getPath();
        txtPath.setText(path);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdfView.fromFile(file)
                .pages(0) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                //.autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
                //.fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                //.pageSnap(false) // snap pages to screen boundaries
                //.pageFling(false) // make a fling change only a single page like ViewPager
                //.nightMode(false) // toggle night mode
                .load();
    }
    private String getPath(){
        String path="";
        JsonFile jsonFile=new JsonFile(this);
        String currDate=null;
        try {
            currDate=jsonFile.getBillJson().getString("updatedDate");
            currDate=currDate.replace(" ","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator +"Ambia_Garden"+File.separator+ "eBill_" +currDate+".pdf";
            path=path.replace(" ","");
            File newDir = new File( getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
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
}