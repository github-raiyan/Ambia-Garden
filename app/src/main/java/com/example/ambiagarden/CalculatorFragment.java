package com.example.ambiagarden;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CalculatorFragment extends Fragment {
    Double var=0.0;
    TextView monitor,operator;
    AppCompatButton btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix,btnSeven,btnEight,btnNine,btnZero,
    btnDel,btnAc,btnMul,btnDiv,btnAdd,btnSub,btnEqual,btnPoint;
    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calculator, container, false);
        findViews(view);
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="1";
                }
                else {
                    s+="1";
                }
                monitor.setText(s);
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="2";
                }
                else {
                    s+="2";
                }
                monitor.setText(s);

            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="3";
                }
                else {
                    s+="3";
                }
                monitor.setText(s);
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="4";
                }
                else {
                    s+="4";
                }
                monitor.setText(s);
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="5";
                }
                else {
                    s+="5";
                }
                monitor.setText(s);
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="6";
                }
                else {
                    s+="6";
                }
                monitor.setText(s);
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="7";
                }
                else {
                    s+="7";
                }
                monitor.setText(s);
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="8";
                }
                else {
                    s+="8";
                }
                monitor.setText(s);
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="9";
                }
                else {
                    s+="9";
                }
                monitor.setText(s);
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.equals("0")){
                    s="0";
                }
                else {
                    s+="0";
                }
                monitor.setText(s);
            }
        });
        btnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                boolean pointHasbefor=false;
                for (int i=0;i<s.length();i++){
                    if(s.charAt(i)=='.')pointHasbefor=true;
                }
                if(!pointHasbefor){
                    s+=".";
                    monitor.setText(s);
                }


            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                s=s.substring(0,s.length()-1);
                if(s.isEmpty()){
                    s="0";
                }
                monitor.setText(s);
            }
        });
        btnAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitor.setText("0");
                var=0.0;
                operator.setText("");
            }
        });
        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                var=Double.parseDouble(monitor.getText().toString());
                operator.setText("x");
                monitor.setText("");
            }
        });
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var=Double.parseDouble(monitor.getText().toString());
                operator.setText("/");
                monitor.setText("");
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                var=Double.parseDouble(monitor.getText().toString());
                operator.setText("+");
                monitor.setText("");
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                var=Double.parseDouble(monitor.getText().toString());
                operator.setText("-");
                monitor.setText("");
            }
        });
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cal();
                }catch (Exception e){

                }

                operator.setText("=");
            }
        });



        return view;
    }
    void cal(){
        if(operator.getText().toString().equals("+")){
            monitor.setText(""+(Double.parseDouble(monitor.getText().toString())+var));
            var=Double.parseDouble(monitor.getText().toString());
        }
        else if(operator.getText().toString().equals("-")){
            monitor.setText(""+(var-Double.parseDouble(monitor.getText().toString())));
            var=Double.parseDouble(monitor.getText().toString());
        }
        else if(operator.getText().toString().equals("x")){
            monitor.setText(""+(Double.parseDouble(monitor.getText().toString())*var));
            var=Double.parseDouble(monitor.getText().toString());
        }
        else if(operator.getText().toString().equals("/")){
            monitor.setText(""+(var/Double.parseDouble(monitor.getText().toString())));
            //System.out.println(""+Double.parseDouble(monitor.getText().toString())+"  var="+var);
            var=Double.parseDouble(monitor.getText().toString());
        }

    }
    void findViews(View view){
        monitor=view.findViewById(R.id.tv_monitor);
        operator=view.findViewById(R.id.tv_operator);
        btnOne=view.findViewById(R.id.btn_one);
        btnTwo=view.findViewById(R.id.btn_two);
        btnThree=view.findViewById(R.id.btn_three);
        btnFour=view.findViewById(R.id.btn_four);
        btnFive=view.findViewById(R.id.btn_five);
        btnSix=view.findViewById(R.id.btn_six);
        btnSeven=view.findViewById(R.id.btn_seven);
        btnEight=view.findViewById(R.id.btn_eight);
        btnNine=view.findViewById(R.id.btn_nine);
        btnZero=view.findViewById(R.id.btn_zero);
        btnDel=view.findViewById(R.id.btn_del);
        btnAc=view.findViewById(R.id.btn_ac);
        btnMul=view.findViewById(R.id.btn_mul);
        btnDiv=view.findViewById(R.id.btn_div);
        btnAdd=view.findViewById(R.id.btn_add);
        btnSub=view.findViewById(R.id.btn_sub);
        btnEqual=view.findViewById(R.id.btn_equal);
        btnPoint=view.findViewById(R.id.btn_point);
    }
}