package com.example.ambiagarden;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Vector;

public class HistoryFragment extends Fragment {
    ListView listView;
    Vector<Record>history=new Vector<>();
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        update();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_history, container, false);

        listView=view.findViewById(R.id.listView);
        update();


        return view;
    }
    void update(){

        DatabaseHelper databaseHelper=new DatabaseHelper(getContext());
        databaseHelper.readData(history);

        int length=history.size();
        String[] date,reading,amount;
        date=new String[length];
        reading=new String[length];
        amount=new String[length];
        for(int i=0;i<length;i++){
            date[i]=history.get(i).date;
            reading[i]=history.get(i).reading;
            amount[i]=history.get(i).amount;
        }



        CustomAdapter adapter=new CustomAdapter(getContext(),date,reading,amount);
        listView.setAdapter(adapter);

    }

}