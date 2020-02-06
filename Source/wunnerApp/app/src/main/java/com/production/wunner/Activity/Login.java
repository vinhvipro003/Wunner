package com.production.wunner.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.Custom.CustomDialog;
import com.production.wunner.Model.Station;
import com.production.wunner.Model.Team;
import com.production.wunner.Model.User;
import com.production.wunner.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    Button btn_login;
    EditText edit_name, edit_pass;
    String user,pass;
    User userData;
    int num_stage;
    String station_ID;
    private static final int MY_LOCATION_REQUEST_CODE = 1977;
    CustomDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
        //check user log in before
        if( preferences.getString("user_name",null)!=null&& preferences.getString("pass",null) !=null)
        {
            String name=preferences.getString("user_name",null);
            String pass= preferences.getString("pass",null);

            Validation(name,pass);
        }
        Mapping();
        setupUI(findViewById(R.id.relative_login));
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user= edit_name.getText().toString();
                pass =edit_pass.getText().toString();
                if(user==""| pass=="")
                {
                    Toast.makeText(getApplicationContext(),"Please complete info",Toast.LENGTH_SHORT).show();
                }
                Validation(user,pass);

            }
        });

    }

    private void ChangeActivity() {

        //send data user to request and fetch data
        // Storage User and pass
         station_ID =getStationID(userData.getUserName());
        num_stage= Integer.parseInt(userData.getLayoutType());
        Intent intent;

       if(num_stage>0)
       {
           FetchDataStaion(station_ID);
       }
       else

        {
             intent =new Intent(Login.this,UserInfo.class);
            intent.putExtra("UserData",userData);
            Login.this.startActivity(intent);
            Login.this.finish();
            if(progressDialog.isShowing())
            {
                progressDialog.hideDialog();
            }

        }



    }

    private String getStationID(String userName) {
        GetWunnerDataService service =RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<ArrayList<String>> call =service.getStationbyUser(userName);
        final ArrayList<String>[] data = new ArrayList[]{new ArrayList()};
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    data[0] = response.body();
                    if(data[0].size()!=0) {
                        Log.d("AAAA", data[0].get(0));
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

            }
        });


        if(data==null || data[0].size()==0)
        {
            return "station1";
        }
        return data[0].get(data[0].size()-1);
    }

    private void UpdateStage(int stage) {
        GetWunnerDataService service =RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<String>  call =service.UpdateLayout(userData.getUserName(), String.valueOf(stage));
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

    private Team FetchTeam(String teamID) {
        final Team[] team = new Team[1];
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<Team> call= service.GetTeam(teamID);
        call.enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if(response.isSuccessful()) {
                    team[0] = response.body();
                }

            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error load station", Toast.LENGTH_SHORT).show();
            }
        });

        return  team[0];
    }

    private boolean Validation(String user, String pass) {
        progressDialog =new CustomDialog(this);
        progressDialog.showDialog();
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        GetWunnerDataService.UserLogin userLogin= new GetWunnerDataService.UserLogin(user,pass);
        Log.d("Tag",new Gson().toJson(userLogin));
        Call<User> call = service.LoginUser(userLogin);

        //Call<User> call =service.LoginUsers(user,pass);
        final boolean[] flag = {false};
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful())
                {
                 gererateUser(response.body());
                 flag[0] =true;
                }
                else
                { Toast.makeText(getApplicationContext(),"Fail to Login",Toast.LENGTH_SHORT).show();
                    if(progressDialog.isShowing())
                    {
                        progressDialog.hideDialog();
                    }
                    SharedPreferences preferences =getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("user_name");
                    editor.remove("pass");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        return flag[0];
    }

    private void gererateUser(User userData) {
        this.userData =userData;
        if(this.userData.getUserType().compareTo("admin")==0)
        {
            UpdateStage(0);
        }
            showPermissionRequest();


    }


    private void Mapping() {
        btn_login =(Button)findViewById(R.id.btn_login);
        edit_name= (EditText) findViewById(R.id.edit_name);
        edit_pass=(EditText) findViewById(R.id.edit_pass);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Login.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    private void showPermissionRequest() {
        if(userData.getUserType().compareTo("admin")==0)
        {
            UpdateStage(0);
        }
        List<String> permissions =new ArrayList<>();
        int fine_location = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int corase_location = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(fine_location!=PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(corase_location!=PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(permissions.isEmpty())
        {
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            ChangeActivity();

        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions.toArray(new String[permissions.size()]),MY_LOCATION_REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case  MY_LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    saveUser();
                     ChangeActivity();
                } else {
                    Toast.makeText( this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences =getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("user_name");
                    editor.remove("pass");
                    editor.commit();
                    finish();
                }
                break;

        }
    }

    private void saveUser() {
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_name",user);
        editor.putString("pass",pass);
        editor.putString("teamID",userData.getTeamID());
        editor.commit();
    }
    private void ChangeActivitywithData(Station station) {
        Intent intent;
        switch (num_stage) {
            case 1: {
                intent = new Intent(Login.this, UserInfo.class);
                UpdateStage(1);
                intent = new Intent(Login.this, MainActivity.class);
                Log.d("Station", station_ID);

                intent.putExtra("UserData", userData);

                intent.putExtra("StationDataRun", station);
                Login.this.startActivity(intent);
                Login.this.finish();
                if (progressDialog.isShowing()) {
                    progressDialog.hideDialog();
                }
                break;
            }
            case 2: {
                intent = new Intent(Login.this, MainActivity.class);

                UpdateStage(2);
                intent.putExtra("StationDataRun", station);
                Login.this.startActivity(intent);
                Login.this.finish();
                if (progressDialog.isShowing()) {
                    progressDialog.hideDialog();
                }
                break;
            }
            case 3: {
                intent = new Intent(Login.this, show_misson.class);
                intent.putExtra("StationDataRun", station);
                UpdateStage(3);
                Login.this.startActivity(intent);
                Login.this.finish();
                if (progressDialog.isShowing()) {
                    progressDialog.hideDialog();
                }
                break;
            }
        }
    }
    private void  FetchDataStaion(String stationID) {
        final Station[] station = new Station[1];
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<Station> call =service.GetStation(stationID);
        try {
            call.enqueue(new Callback<Station>() {
                @Override
                public void onResponse(Call<Station> call, Response<Station> response) {
                    if(response.isSuccessful())
                    {

                        ChangeActivitywithData(response.body());


                    }
                    else
                    {
                    }
                }

                @Override
                public void onFailure(Call<Station> call, Throwable t) {

                }
            });
        }catch (Exception ex)
        {
            Log.d("AAAAAA",ex.toString());
        }


    }


}