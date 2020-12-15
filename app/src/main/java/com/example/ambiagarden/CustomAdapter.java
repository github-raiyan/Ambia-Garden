package com.example.ambiagarden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    String[] date,reading,amount;
    private LayoutInflater layoutInflater;
    public CustomAdapter(Context context,String[] date,String[] reading,String[] amount) {
        this.context = context;
        this.date=date;
        this.reading=reading;
        this.amount=amount;
    }

    Context context;
    @Override
    public int getCount() {
        return date.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.layout,parent,false);

        }
        //System.out.println("-------->>>>>>>>>"+convertView);
        TextView datetxt =(TextView) convertView.findViewById(R.id.txt_date);
        TextView readingtxt=convertView.findViewById(R.id.readingtxt);
        TextView amounttxt=convertView.findViewById(R.id.amounttxt);

        datetxt.setText(date[position]);
        readingtxt.setText(reading[position]);
        amounttxt.setText(amount[position]);

        return convertView;
    }
}
