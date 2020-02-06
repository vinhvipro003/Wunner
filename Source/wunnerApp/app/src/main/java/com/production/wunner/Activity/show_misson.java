package com.production.wunner.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.instacart.library.truetime.TrueTime;
import com.production.wunner.Api.GetWunnerDataService;
import com.production.wunner.Api.RetrofitInstance;
import com.production.wunner.Custom.CustomDialog;
import com.production.wunner.Model.Station;
import com.production.wunner.R;
import com.production.wunner.Adapter.Tab_Layout_Adapter;
import com.production.wunner.Fragment.fragment_rated;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.util.Date;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;

public class show_misson extends AppCompatActivity {
    TabLayout mTabs;
    View mIndicator;
    ViewPager mViewPager;
    Station station;
    private int indicatorWidth;
    CustomDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_misson);
        Intent intent =getIntent();
        station= (Station) intent.getSerializableExtra("StationDataRun");
        mTabs = findViewById(R.id.tabLayout);
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);
        UpdateStage(3);
        getSupportActionBar().hide();
        dialog= new CustomDialog(this);

        dialog.showDialog();
        //Set up the view pager and fragments

         new RetrieveFeedTask().execute("AAAA");








    }

    private void UpdateStage(int stage) {
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
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
    class RetrieveFeedTask extends AsyncTask<String, Void, Long> {

        private Exception exception;

        protected Long doInBackground(String... urls) {
            long a=0;
            try {
                a =getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return a;
        }

        protected void onPostExecute(Long  feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            long timeend=0;
            SharedPreferences preferences =getApplicationContext().getSharedPreferences("Timer",MODE_PRIVATE);
            if(preferences.getLong("Gettime",0)!=0 && preferences.getLong("Gettime",0)!=0)
            {
                Log.d("Ahuhu1", String.valueOf(feed));
                long timer= preferences.getLong("Gettime",0);
                timeend=(feed-timer)/1000;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("Gettime", feed);
                Log.d("Ahuhu", String.valueOf(timeend));

            }
            else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("Gettime", feed);
                editor.commit();
                Log.d("Ahuhu", String.valueOf(feed));

            }


            if(dialog.isShowing())
            {
                dialog.hideDialog();
            }




            Tab_Layout_Adapter adapter = new Tab_Layout_Adapter(getSupportFragmentManager());
            adapter.addFragment(Misson.newInstance(getApplicationContext(),station,timeend), "Mission");
            adapter.addFragment(fragment_rated.newInstance(getApplicationContext()), "Rated");
            mViewPager.setAdapter(adapter);
            mTabs.setupWithViewPager(mViewPager);
            mTabs.post(new Runnable() {
                @Override
                public void run() {
                    indicatorWidth = mTabs.getWidth() / mTabs.getTabCount();

                    //Assign new width
                    FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
                    indicatorParams.width = indicatorWidth;
                    mIndicator.setLayoutParams(indicatorParams);
                }
            });
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mIndicator.getLayoutParams();

                    //Multiply positionOffset with indicatorWidth to get translation
                    float translationOffset =  (positionOffset+position) * indicatorWidth ;
                    params.leftMargin = (int) translationOffset;
                    mIndicator.setLayoutParams(params);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
    private long getTime() throws Exception {
        String url = "https://time.is/Unix_time_now";
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        String[] tags = new String[] {
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements= doc.select(tags[0]);
        for (int i = 0; i <tags.length; i++) {
            elements = elements.select(tags[i]);
        }
        return Long.parseLong(elements.text() + "000");
    }
    }





