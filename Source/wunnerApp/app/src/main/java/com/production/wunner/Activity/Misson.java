package com.production.wunner.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.Model.Station;
import com.production.wunner.R;
import com.production.wunner.TimeCounterService;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class Misson extends Fragment {
    private Context context;
    ProgressBar progressBarView;
    CountDownTimer countDownTimer;
    int endTime = 1800;
    TextView txt_timer,txt_mission;
    Button btn_submit;
    DatabaseReference reference;
    Station station;
    String teamID="";
    long timer;
    boolean serviceOff=false;
    public static Misson newInstance(Context context, Station station,long timer) {
        return new Misson(context,station,timer );
    }
    public Misson(Context context, Station station,long timer) {
        this.timer= timer;
        this.context = context;
        this.station=station;
    }

    @Override
    public View onCreateView(  LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_misson, container, false);
        //setContentView(R.layout.activity_misson);
        // Toolbar toolbar = findViewById(R.id.toolbar);
        txt_timer =view.findViewById(R.id.txt_timer);
        txt_mission=view.findViewById(R.id.txt_mission);
        btn_submit= view.findViewById(R.id.btn_submit);

        endTime= (int) station.getStationTime();
        endTime= (int) (endTime-timer);
        SharedPreferences preferences =context.getSharedPreferences("Login",MODE_PRIVATE);
        teamID= preferences.getString("teamID",null);
        // setSupportActionBar(toolbar);

        progressBarView = (ProgressBar) view.findViewById(R.id.view_progress_bar);
        txt_mission.setText(station.getStationDescription());
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);
        Intent intent =new  Intent(context, TimeCounterService.class);
        intent.putExtra("count_timer",(long) endTime);
        context.startService(intent);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =context.getSharedPreferences("Timer",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("Gettime");
                editor.commit();
                UpdateStage(0);
                GetWunnerDataService service =RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
                Call<String> call =service.SubmitTimer(teamID);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(context,"Congratulation. Please wait to check ",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
                if(!serviceOff) {
                    serviceOff=true;
                    context.unregisterReceiver(receiver);
                    context.stopService(new Intent(context, TimeCounterService.class));
                }
                reference= FirebaseDatabase.getInstance().getReference(station.getStationID());
                Log.d("StationID", station.getStationID());
                reference.setValue("team1");
                //TODO tý nữa nhớ sửa "team1" là team id nha.
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String valume =dataSnapshot.getValue(String.class);
                        if(valume.compareTo("Rated")==0)
                        {
                          HandlerResult();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return view;


    }

    private void HandlerResult() {

        reference.setValue("Updated");
        Intent intent =new Intent(getContext(),UserInfo.class);
        startActivity(intent);
    }


    private void UpdateStage(int stage) {
        SharedPreferences preferences =context.getSharedPreferences("Login",MODE_PRIVATE);
        String name=preferences.getString("user_name",null);
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<String> call =service.UpdateLayout(name, String.valueOf(stage));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Update Layout",response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }



    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateTimerView(intent);
        }
    };

    private void UpdateTimerView(Intent intent) {
        long timer =intent.getExtras().getLong("responetimer");
        setProgress((int)timer,endTime);
        txt_timer.setText(new SimpleDateFormat("mm:ss").format(new Date(timer)));
        if(timer<=0)
        {
            Toast.makeText(context,"Time out. Fighting", Toast.LENGTH_SHORT).show();
            UpdateStage(0);
            SharedPreferences preferences =context.getSharedPreferences("Timer",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("Gettime");
            editor.commit();

        }
    }

    private void setProgress(int myProgress, long time) {
        progressBarView.setMax((int) time);
        progressBarView.setSecondaryProgress( (int) time);
        progressBarView.setProgress(myProgress/1000);
        txt_timer.setText(new SimpleDateFormat("mm:ss").format(new Date(time-myProgress)));
        if(myProgress<=0)
        {
            if(!serviceOff) {
                context.stopService(new Intent(context, TimeCounterService.class));
            }
            UpdateStage(0);
            SharedPreferences preferences =context.getSharedPreferences("Timer",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("Gettime");
            editor.commit();
        }

    }
    @Override
    public void onResume() {
        context.registerReceiver(receiver, new IntentFilter(TimeCounterService.COUNTTIMER_BR));
        super.onResume();
    }

    @Override
    public void onPause() {
            try {
                context.unregisterReceiver(receiver);
            }
            catch (Exception ex ) {

            }
        super.onPause();
    }
    @Override
    public void onStop() {
        try {
            context.unregisterReceiver(receiver);
        }
        catch (Exception ex )
        {

        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        context.stopService(new Intent(context, TimeCounterService.class));
        super.onDestroy();
    }
}
