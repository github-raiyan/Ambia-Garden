package com.example.ambiagarden;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    final String[] widgetNames={"তারিখঃ ","মিটার নংঃ ","প্রতি ইউনিটঃ ","পূর্বের রিডিংঃ ","বর্তমান রিডিংঃ ",
            "বর্তমান ব্যাল্যান্সঃ ","গড় মাসিক ব্যয়ঃ ","ব্যাল্যান্স শেষ হবার সম্ভাব্যঃ  ","সর্বশেষ জমাঃ ","চালাতে পারবেঃ "};
    TextView txtDate,txtMeterNO,txtPerUnit,txtPreReading,txtCurrReading,
            txtCurrBalance,txtAvgBill,txtPossibleDate,txtLastRecharge,txtCanUse,loantxt;
    ImageButton update;
    ProgressBar progressBar;
    UpdateAll updateAll;
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


        return view;
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
        loantxt=view.findViewById(R.id.loadtxt);
        update=view.findViewById(R.id.btn_update);
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
            txtPreReading.setText(widgetNames[3]+"   "+billJSON.getString("prevReading"));
            txtCurrReading.setText(widgetNames[4]+"   "+billJSON.getString("currReading"));
            txtCurrBalance.setText(widgetNames[5]+"   "+billJSON.getString("remainingAmount")+"  টাকা");
            txtAvgBill.setText(widgetNames[6]+"   "+billJSON.getString("avgMonthly")+"  টাকা");
            txtLastRecharge.setText(widgetNames[8]+"   "+billJSON.getString("lastRecharge")+"  টাকা");
            txtCanUse.setText(widgetNames[9]+"   "+billJSON.getString("canUseUntil")+"");
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