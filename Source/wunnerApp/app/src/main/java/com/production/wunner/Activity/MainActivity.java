package com.production.wunner.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.production.wunner.Adapter.Tab_Layout_Adapter;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.AsyncLoadLatLng;
import com.production.wunner.Custom.CustomDialog;
import com.production.wunner.Fragment.fragment_rated;
import com.production.wunner.Interface.GetCoordinates;
import com.production.wunner.Model.Location;
import com.production.wunner.Model.Station;
import com.production.wunner.R;
import com.production.wunner.TimeCounterService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap ggmap;
    private MapView mapView;
    private Boolean RemoveService = false;
    private String teamID;
    private ArrayList<Location> data = new ArrayList<>();
    private static final String MAP_VIEW_BUNDEL_KEY = "MapViewBundleKey";
    ArrayList<String> name = new ArrayList<>();
    TextView txt_name_location, txt_timer, txt_distance, txt_postion_location;
    CountDownTimer countDownTimer;
    //private int timedown=1000;
    private int timedown = 1800;
    private Station station;
    CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        teamID = preferences.getString("teamID", null);
        station = (Station) intent.getSerializableExtra("StationDataRun");
        station.setCheckin(false);
        dialog=new CustomDialog(this);
        dialog.showDialog();
        new RetrieveFeedTask().execute("AAAAA");


        mapView = findViewById(R.id.map_view);

        txt_name_location = findViewById(R.id.txt_name_location);
        txt_timer = findViewById(R.id.txt_count_timer);
        txt_distance = findViewById(R.id.txt_distance);
        txt_postion_location = findViewById(R.id.txt_postion_location);

        Bundle mapviewBundle = null;
        if (savedInstanceState != null) {
            mapviewBundle = (Bundle) savedInstanceState.get(MAP_VIEW_BUNDEL_KEY);
        }
        mapView.onSaveInstanceState(mapviewBundle);
        mapView.onCreate(mapviewBundle);

        UpdateStage(2);


    }

    private void UpdateStage(int stage) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        String name = preferences.getString("user_name", null);
        String teamID = preferences.getString("teamID", null);
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<String> call = service.UpdateLayout(name, String.valueOf(stage));
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateTextTime(intent);
        }
    };

    private void UpdateTextTime(Intent intent) {
        long timer = intent.getExtras().getLong("responetimer");
        Log.d("Timer", String.valueOf(timer));
        txt_timer.setText(new SimpleDateFormat("mm:ss").format(new Date(timer)));
        if (timer <= 0) {
            Toast.makeText(MainActivity.this, "Time out. Fighting", Toast.LENGTH_SHORT).show();
            SharedPreferences preferences =getApplicationContext().getSharedPreferences("Timer",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("Gettime");
            editor.commit();
            station.setCheckin(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDEL_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDEL_KEY, mapViewBundle);
        }

    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter(TimeCounterService.COUNTTIMER_BR));
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception ex) {

        }
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception ex) {

        }
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!RemoveService) {
            this.stopService(new Intent(MainActivity.this, TimeCounterService.class));
        }
        mapView.onDestroy();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ggmap = googleMap;
        ggmap.setMinZoomPreference(12);
        ggmap.setMaxZoomPreference(30);
        fetchData();
        ggmap.setMyLocationEnabled(true);
        Intent intent = new Intent(MainActivity.this, TimeCounterService.class);
        intent.putExtra("count_timer", (long) timedown);
        startService(intent);
        new AsyncLoadLatLng(this, name, new GetCoordinates() {
            @Override
            public void UpdatLatLng(final ArrayList<LatLng> list) {

                LatLng temp = new LatLng(Double.parseDouble(station.getStationLatitude()), Double.parseDouble(station.getStationLongtitude()));
                station.setLatLng(temp);
                Circle circle = ggmap.addCircle(new CircleOptions()
                        .center(temp)
                        .radius(100)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(50, 100, 0, 0)));
                Marker marker = ggmap.addMarker(new MarkerOptions().position(temp));
                station.setMarker(marker);
                station.getMarker().setTitle(station.getStationName());
                ggmap.moveCamera(CameraUpdateFactory.newLatLng(temp));

                if (ggmap != null) {
                    ggmap.setOnMarkerClickListener(MainActivity.this);
                    ggmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(android.location.Location location) {

                            android.location.Location loc2 = new android.location.Location("");
                            loc2.setLatitude(station.getLatLng().latitude);
                            loc2.setLongitude(station.getLatLng().longitude);
                            int distanceInMeters = (int) location.distanceTo(loc2) / 1000;
                            txt_distance.setText("" + distanceInMeters);
                            float[] distance = new float[2];
                            if (location != null & loc2 != null) {
                                android.location.Location.distanceBetween(location.getLatitude(), location.getLongitude(), loc2.getLatitude(), loc2.getLongitude(), distance);
                                if (distance[0] < 100) {
                                    station.setCheckin(true);
                                    SharedPreferences preferences =getApplicationContext().getSharedPreferences("Timer",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.remove("Gettime");
                                    editor.commit();
                                    Toast.makeText(MainActivity.this, "Congratulation!!! New Mission......Please clicking marker", Toast.LENGTH_SHORT).show();
                                    //CheckIn();


                                }
                            }
                        }
                    });
                }
                txt_name_location.setText(station.getStationName());
                txt_postion_location.setText("1st Station");


            }
        }).execute();
    }

    private void CheckIn() {
        stopService(new Intent(MainActivity.this, TimeCounterService.class));

        RemoveService = true;
        FetchData(station.getStationID());


    }

    private void FetchData(String stationID) {
        GetWunnerDataService service = RetrofitInstance.getRetrofitInstance().create(GetWunnerDataService.class);
        Call<Station> call = service.GetStation(stationID);
        call.enqueue(new Callback<Station>() {
            @Override
            public void onResponse(Call<Station> call, Response<Station> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), show_misson.class);
                    intent.putExtra("StationDataRun", response.body());
                    UpdateStage(3);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<Station> call, Throwable t) {

            }
        });
    }



    private void fetchData() {
        //name.add( "Cong vien 30/4");
        //name.add("Ho con rua");
        //name.add("Bao tang Phu nu Nam Bo");
        name.add("Truong dai hoc khoa hoc tu nhien");
        name.add("Hồ Con Rùa");

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.equals(station.getMarker())) {
            if (station.getCheckin()) {
                CheckIn();
            }
        }
        return false;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Long> {

        private Exception exception;

        protected Long doInBackground(String... urls) {
            long a = 0;
            try {
                a = getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return a;
        }

        protected void onPostExecute(Long feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            long timeend = 0;
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("Timer", MODE_PRIVATE);
            if (preferences.getLong("Gettime", 0) != 0 && preferences.getLong("Gettime", 0) != 0) {

                long timer = preferences.getLong("Gettime", 0);
                timeend = (feed - timer) / 1000;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("Gettime", feed);

            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("Gettime", feed);
                editor.commit();

            }
            timedown= (int) (timedown-timeend);

            if (dialog.isShowing()) {
                dialog.hideDialog();
            }

            mapView.getMapAsync(MainActivity.this);

        }


    }
    private long getTime() throws Exception {
        String url = "https://time.is/Unix_time_now";
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        String[] tags = new String[]{
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements = doc.select(tags[0]);
        for (int i = 0; i < tags.length; i++) {
            elements = elements.select(tags[i]);
        }
        return Long.parseLong(elements.text() + "000");
    }
}
