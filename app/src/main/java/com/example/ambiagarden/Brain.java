package com.example.ambiagarden;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class Brain {
    private final List<String> equation = new Vector<>();
    private int start,end;
    private final double eps=0.000000001;

    public Brain(String s){
        // TODO Auto-generated constructor stub
        extract(s);

    }
    public String result() {
        calBrackets();


        start=0;end=equation.size();
        bodmas();
        return equation.get(0);
    }
    private void extract(String s) {
        if(s.charAt(0)=='-'){
            s="(0"+s+")";
        }
        s="1*"+s+"*1";
        String part="";
        for(int i=0;i<s.length();i++) {
            if(isoperator(s.charAt(i))) {
                equation.add(part);
                equation.add(String.valueOf(s.charAt(i)));
                part="";
            }

            else {
                part+=s.charAt(i);
            }

        }
        equation.add(part);
        rmEmptyString();
    }

    private boolean isoperator(char c) {
        return c=='+'||c=='-'||c=='*'||c=='/'||c=='^'||c=='!'||c=='√'||c=='('||c==')'||c=='C'||c=='P'||c=='π';
    }

    private void rmEmptyString() {
        for(int i=0;i<equation.size();i++) {
            if(equation.get(i)=="") {
                equation.remove(i);
                i--;
            }
        }
    }
    private void calBrackets() {//    (........)+(....*(.....)) = ....+(...*(...))
        for(int i=0;i<equation.size();i++) {
            String str = equation.get(i);
            if(str.equals(")")) {
                for(int j=i;j>=0;j--) {
                    String str2=equation.get(j);
                    if(str2.equals("(")) {
                        //prntList();
                        //System.out.println("j="+(j+1)+" i="+i);
                        start=j+1;end=i;
                        bodmas();
                        int endBracket=j;
                        while(!str2.equals(")")) str2=equation.get(++endBracket);
                        if(end!=endBracket)System.out.println("not same");
                        str2=equation.get(endBracket+1);
                        Character c =str2.charAt(str2.length()-1);
                        int low=Character.compare(c, '0');
                        int high=Character.compare(c, '9');
                        if(low>=0&&high<=0){
                            equation.set(endBracket,"*");
                        }
                        else {
                            equation.remove(endBracket);
                        }
                        str2=equation.get(j-1);
                        c =str2.charAt(str2.length()-1);
                        low=Character.compare(c, '0');
                        high=Character.compare(c, '9');
                        if(low>=0&&high<=0){
                            equation.set(j,"*");
                        }
                        else {
                            equation.remove(j);
                        }
                        i=0;
                        break;
                    }
                }
            }
        }
    }
    private void bodmas() {//brackets/of/div/mul/add/sub
        //i'm sure that there is not brackets in this range.
        //prntList();
        //System.out.println("start="+start+(" end="+end));
        pi();
        sin();
        cos();
        tan();
        factorial();
        log();
        root();
        toThePower();
        nCr_nPr();
        //power
        div();
        mul();
        add_sub();
        //sub();
    }
    private void pi(){
        for(int i=start;i<end;i++){
            if(equation.get(i).equals("π")){
                equation.set(i,""+Math.PI);
                if(!isoperator(equation.get(i-1).charAt(0))){
                    equation.add(i,"*");
                    end+=1;
                }
            }
        }
    }
    private void sin() {// sin' 30'   /   sin' ^' -' 30'
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("sin")) {
                if(equation.get(i+1).equals("^")) {
                    //sin inverse
                    double a= Math.asin(Double.parseDouble(equation.get(i+3)))+eps;
                    a=Math.toDegrees(a);
                    equation.set(i, ""+a);
                    equation.remove(i+3);
                    equation.remove(i+2);
                    end-=2;
                }
                else {
                    //sin
                    double a= Math.sin(Math.toRadians(Double.parseDouble(equation.get(i+1))))+eps;
                    equation.set(i, ""+a);
                }
                equation.remove(i+1);
                end-=1;
            }
        }
    }
    private void cos() {
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("cos")) {
                if(equation.get(i+1).equals("^")) {
                    double a= Math.acos(Double.parseDouble(equation.get(i+3)))+eps;
                    a=Math.toDegrees(a);
                    equation.set(i, ""+a);
                    equation.remove(i+3);
                    equation.remove(i+2);
                    end-=2;
                }
                else {
                    double a= Math.cos(Math.toRadians(Double.parseDouble(equation.get(i+1))))+eps;
                    equation.set(i, ""+a);
                }
                equation.remove(i+1);
                end-=1;
            }
        }
    }
    private void tan() {
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("tan")) {
                if(equation.get(i+1).equals("^")) {
                    double a= Math.atan(Double.parseDouble(equation.get(i+3)))+eps;
                    a=Math.toDegrees(a);
                    equation.set(i, ""+a);
                    equation.remove(i+3);
                    equation.remove(i+2);
                    end-=2;
                }
                else {
                    double a= Math.tan(Math.toRadians(Double.parseDouble(equation.get(i+1))))+eps;
                    equation.set(i, ""+a);
                }
                equation.remove(i+1);
                end-=1;
            }
        }
    }
    private long fact(long x) {
        if(x==0)return 1;
        return x * fact(x-1);
    }
    private void factorial() {
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("!")) {
                long x=Long.parseLong(equation.get(i-1));
                equation.set(i-1, ""+fact(x));
                equation.remove(i);
                end-=1;
            }
        }
    }
    private void log() { // log'  25'
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("log")) {
                double a= Math.log10(Double.parseDouble(equation.get(i+1)));
                equation.set(i, ""+a);
                equation.remove(i+1);
                end-=1;
            }
            else if(equation.get(i).equals("ln")) {
                double a= Math.log(Double.parseDouble(equation.get(i+1)));
                equation.set(i, ""+a);
                equation.remove(i+1);
                end-=1;
            }
        }

    }
    private void root() {
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("√")) {
                double a=Math.sqrt(Double.parseDouble(equation.get(i+1)));
                String str=equation.get(i-1);
                Character c =str.charAt(str.length()-1);
                int low=Character.compare(c, '0');
                int high=Character.compare(c, '9');
                if(low>=0&&high<=0){
                    equation.set(i, "*");
                    equation.set(i+1, ""+a);
                }
                else {
                    equation.set(i, ""+a);
                    equation.remove(i+1);
                    end-=1;
                }
            }
        }
    }
    private void toThePower() {
        //sure that there is no sin/cos/tan inverse.
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("^")) {
                if(equation.get(i+1).equals("-")) {
                    double p=Double.parseDouble(equation.get(i+1)+equation.get(i+2));
                    double n=Double.parseDouble(equation.get(i-1));
                    equation.set(i-1, ""+Math.pow(n, p));
                    equation.remove(i+2);
                    equation.remove(i+1);
                    equation.remove(i);
                    end-=3;
                }
                else {//5^2
                    double p=Double.parseDouble(equation.get(i+1));
                    double n=Double.parseDouble(equation.get(i-1));
                    equation.set(i-1, ""+Math.pow(n, p));
                    equation.remove(i+1);
                    equation.remove(i);
                    end-=2;
                }

            }
        }
    }
    private void nCr_nPr() {
        for(int i=start;i<end;i++) {
            if(equation.get(i).equals("C")) {
                long n=Long.parseLong(equation.get(i-1));
                long r=Long.parseLong(equation.get(i+1));
                equation.set(i-1,""+(fact(n)/(fact(r)*fact(n-r))));
                equation.remove(i+1);
                equation.remove(i);
                end-=2;
            }
            else if(equation.get(i).equals("P")){
                long n=Long.parseLong(equation.get(i-1));
                long r=Long.parseLong(equation.get(i+1));
                equation.set(i-1,""+(fact(n)/fact(n-r)));
                equation.remove(i+1);
                equation.remove(i);
                end-=2;
            }
        }
    }
    private void div() {
        for(int i=start;i<end&& i<equation.size();i++) {
            String str=equation.get(i);
            if(str.equals("/")) {
                //prntList();
                //System.out.println("a="+equation.get(i-1)+" b="+equation.get(i+1)+" i="+i);
                double a=Double.parseDouble(equation.get(i-1));
                double b=Double.parseDouble(equation.get(i+1));
                equation.set(i-1,""+a/b);
                equation.remove(i);
                equation.remove(i);
                i--;
                end-=2;
            }
        }
    }
    private void mul() {
        for(int i=start;i<end&& i<equation.size();i++) {
            String str=equation.get(i);
            if(str.equals("*")) {
                //prntList();
                //System.out.println("a="+equation.get(i-1)+" b="+equation.get(i+1)+" start="+start+" end="+end);
                double a=Double.parseDouble(equation.get(i-1));
                double b=Double.parseDouble(equation.get(i+1));
                equation.set(i-1,""+a*b);
                equation.remove(i);
                equation.remove(i);
                i--;
                end-=2;
            }
        }
    }
    private void add_sub() {
        for(int i=start;i<end && i<equation.size();i++) {

            String str=equation.get(i);
            if(str.equals("+")) {

                double a=Double.parseDouble(equation.get(i-1));
                double b=Double.parseDouble(equation.get(i+1));
                equation.set(i-1,""+(a+b));
                equation.remove(i);
                equation.remove(i);
                i--;
                end-=2;
            }
            else if(str.equals("-")) {

                double a=Double.parseDouble(equation.get(i-1));
                double b=Double.parseDouble(equation.get(i+1));
                equation.set(i-1,""+(a-b));
                equation.remove(i);
                equation.remove(i);
                i--;
                end-=2;
            }
        }
    }

    public boolean hasValidBrackets() {
        Stack<Character>st=new Stack<Character>();
        for(int i=0;i<equation.size();i++) {
            String str = equation.get(i);
            Character c = str.charAt(0);
            if(c.equals('(')){
                st.push(c);

            }
            else if(c.equals(')')) {
                if(st.empty())
                    return false;
                c=st.peek();
                if(!c.equals('(')) {
                    return false;
                }
                else {
                    st.pop();
                }
            }
        }
        return st.empty();
    }
    public void prntList() {
        for(int i=0;i<equation.size();i++) {
            System.out.print(" "+equation.get(i)+"'"+"\n");
        }
        System.out.print("\n");
    }
    private void p(String s) {
        System.out.println(s);
    }
}