package com.example.ambiagarden;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorFragment extends Fragment {

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
                s+=".";
                monitor.setText(s);
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=monitor.getText().toString();
                if(s.isEmpty()){
                    return;
                }
                s=s.substring(0,s.length()-1);
                if(s.isEmpty()){
                    s="";
                }
                monitor.setText(s);
            }
        });
        btnAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitor.setText("");
            }
        });
        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLastCharIsOperator()){
                    String s=monitor.getText().toString();
                    monitor.setText(s+"*");
                }

            }
        });
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLastCharIsOperator()){
                    String s=monitor.getText().toString();
                    monitor.setText(s+"/");
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isLastCharIsOperator()){
                    String s=monitor.getText().toString();
                    monitor.setText(s+"+");
                }
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLastCharIsOperator()){
                    String s=monitor.getText().toString();
                    monitor.setText(s+"-");
                }
            }
        });
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printResult();
            }
        });

        return view;
    }
    void printResult(){
        String s=monitor.getText().toString();
        if(s.isEmpty()){
            return;
        }
        try {
            Brain brain=new Brain(s);
            Double result=Double.parseDouble(brain.result());
            //System.out.println("the result is->>>>>"+result);
            BigDecimal bd= new BigDecimal(result);
            bd  = bd.setScale(3, RoundingMode.HALF_UP);
            //System.out.println("the BIgresult is->>>>>"+bd);
            if(bd.intValue()==bd.doubleValue()){
                monitor.setText(""+bd.intValue());
            }
            else{
                monitor.setText(""+bd.doubleValue());
            }

        }catch (Exception e){
            monitor.setText("Math Error");
        }
    }
    boolean isLastCharIsOperator(){
        String s=monitor.getText().toString();
        if(s.isEmpty())return false;
        char c=s.charAt(s.length()-1);
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    void findViews(View view){
        monitor=view.findViewById(R.id.tv_monitor);
        //operator=view.findViewById(R.id.tv_operator);
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