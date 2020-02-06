package com.production.wunner.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.production.wunner.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragment_sumary extends Fragment {
    Context context;
    String team_id;
    private TextView txt_teamID,txt_StationID,txt_Timer;
    private ArrayList<String> list=new ArrayList();
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    public fragment_sumary(final Context context, final String station_id) {
        this.context = context;
        this.list= (ArrayList<String>) list;
        if(database!=null) {
            reference = database.getReference(station_id.toString());
            reference.setValue("Updated");

        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String valume =dataSnapshot.getValue(String.class);
                if (valume.compareTo("Updated")!=0 && valume.compareTo("Rated")!=0)
                {
                    //todo team id nè
                    team_id=valume;
                    txt_StationID.setText(station_id);
                    txt_teamID.setText(team_id);
                    int timer= Integer.parseInt("120");
                    txt_Timer.setText((new SimpleDateFormat("mm:ss").format(timer)));

                    // SubmitScore();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Failed to read value.",Toast.LENGTH_SHORT);
            }
        });
    }

    public static fragment_sumary newInstance(Context context,String station_id )
    {
        return new fragment_sumary(context,station_id);
    }

    public void UpdateData(List<String> data)
    {
        list= (ArrayList<String>) data;
        txt_teamID.setText(list.get(0));
        txt_StationID.setText(list.get(1));
        int timer= Integer.parseInt(list.get(2));
        txt_Timer.setText((new SimpleDateFormat("mm:ss").format(timer)));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_sumary,container,false);
        txt_StationID=view.findViewById(R.id.txt_stationID);
        txt_teamID=view.findViewById(R.id.txt_teamID);
        txt_Timer=view.findViewById(R.id.txt_timeSubmit);

        return view;
    }
}
