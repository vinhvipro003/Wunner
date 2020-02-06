package com.production.wunner.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragment_record extends Fragment {
    private static EditText edit_Score;
    Context context;
    private static EditText edit_teamID;
    private EditText edit_StationID;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private String team_id;

    public fragment_record(final Context context, final String station_id) {
        this.context = context;
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
                    edit_StationID.setText(station_id);
                    edit_teamID.setText(team_id);

                   // SubmitScore();
                }
                if (valume.compareTo("Rated")==0)
                {
                    submit(station_id,team_id);
                    Toast.makeText(context,"Post successfull",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Failed to read value.",Toast.LENGTH_SHORT);
            }
        });
    }

    public static fragment_record newInstance(Context context,String station_id)
    {
        return new fragment_record(context,station_id);
    }

    public void submit(String station_id, String team_id) {
        station_id = String.valueOf(edit_StationID.getText());
        team_id= edit_teamID.getText().toString();
        String point = String.valueOf(edit_Score.getText());
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<String> call =service.SubmitPoint(team_id,station_id,team_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(context,"Succesfull",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_submit_record,container,false);
        edit_Score=view.findViewById(R.id.edit_Score);
        edit_StationID=view.findViewById(R.id.edit_StationID);
        edit_teamID=view.findViewById(R.id.edit_teamID);

        return view;
    }
}
