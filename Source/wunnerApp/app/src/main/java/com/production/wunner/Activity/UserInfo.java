package com.production.wunner.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.Custom.CustomDialog;
import com.production.wunner.Item;
import com.production.wunner.Model.Station;
import com.production.wunner.Model.User;
import com.production.wunner.R;
import com.production.wunner.Adapter.RecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfo extends AppCompatActivity  {
    TextView txt_UserName,txt_Address,txt_NameNavi;
    Button btn_start;
    RecyclerAdapter adapter;
    RecyclerView.LayoutManager manager;
    RecyclerView recyclerView;
    ArrayList<Item> data=new ArrayList<>();
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private CustomDialog dialog;
    User userdata;
    int[] myImageList = {R.drawable.gradient1, R.drawable.gradient2,R.drawable.gradient3,
            R.drawable.gradient4,R.drawable.gradient5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);
        Bundle extras= getIntent().getExtras();
        if(extras!=null) {
           userdata = (User) getIntent().getSerializableExtra("UserData");
           UpdateUser(userdata);

        }
        updateStage(1);
        txt_UserName =findViewById(R.id.txt_userName);
        txt_Address =findViewById(R.id.txt_Address);

        if(userdata!=null) {
            txt_UserName.setText(userdata.getUserFullname());
            txt_Address.setText(userdata.getUserAddress());
        }
        btn_start =findViewById(R.id.btn_start);
        recyclerView =findViewById(R.id.list_item);
        manager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        UpdateData();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userdata.getUserType().compareTo("admin")==0) {
                    Intent intent = new Intent(getApplicationContext(), Submit.class);
                    UpdateStage(0);
                    String data ="station1";
                    intent.putExtra("station_id",data);
                    startActivity(intent);
                    UserInfo.this.finish();
                }
                else {
                    dialog = new CustomDialog(UserInfo.this);
                    dialog.showDialog();
                    FetchDataStation("station1");
                }
            }
        });
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nv);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Toast.makeText(UserInfo.this, "My Account",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(UserInfo.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.Logout:
                        RemoveAccount();
                        Toast.makeText(UserInfo.this, "Logout",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        UserInfo.this.finish();
                        break;


                    default:
                        return true;
                }


                return true;

            }
        });
    }

    private void UpdateUser(User userdata) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("user_name",userdata.getUserName());
        editor.putString("pass",userdata.getUserPassword());
        editor.putString("teamID",userdata.getTeamID());
        editor.commit();
        //Toast.makeText(this,userdata.getUserID(),Toast.LENGTH_SHORT).show();
    }

    private void RemoveAccount() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.remove("user_name");
        editor.remove("pass");
        editor.commit();
    }

    private void FetchDataStation(String station) {
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<Station> call =service.GetStation(station);
        call.enqueue(new Callback<Station>() {
            @Override
            public void onResponse(Call<Station> call, Response<Station> response) {
                if(response.isSuccessful())
                {
                    StartStation(response.body());
                }
            }

            @Override
            public void onFailure(Call<Station> call, Throwable t) {

            }
        });
    }

    private void StartStation(Station response) {
         Intent intent =new Intent(UserInfo.this,MainActivity.class);
         intent.putExtra("StationDataRun",response);
         UserInfo.this.startActivity(intent);
         UserInfo.this.finish();
         dialog.hideDialog();
    }

    private void UpdateData() {
        data.add(new Item("Bí Ẩn Sài Thành","23/1/2020"));
        data.add(new Item("Bản Lĩnh IT","1/4/2020"));
        data.add(new Item("Thách Thức 2019","2/3/2019"));
        adapter =new RecyclerAdapter(this, data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void updateStage(int i) {
        SharedPreferences.Editor editor =getSharedPreferences("Stage",MODE_PRIVATE).edit();
        editor.putInt("Num_Stage",i);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    private void UpdateStage(int stage) {
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<String> call = service.UpdateLayout(userdata.getUserName(), String.valueOf(stage));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Update Layout", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


}
