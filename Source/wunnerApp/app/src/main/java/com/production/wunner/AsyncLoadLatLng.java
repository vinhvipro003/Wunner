package com.production.wunner;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.production.wunner.Interface.GetCoordinates;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AsyncLoadLatLng extends AsyncTask<Void,Void, ArrayList<LatLng>> {
    Context context;
    Geocoder geocoder;
    GetCoordinates getCoordinates;
    ArrayList<LatLng> list= new ArrayList<>();
    List<String> Name;
    ProgressDialog dialog;
    public AsyncLoadLatLng(Context context,List<String> name,GetCoordinates getCoordinates ) {
        this.context = context;
        geocoder=new Geocoder(context);
        this.Name=name;
        this.getCoordinates=getCoordinates;
    }

    @Override
    protected ArrayList<LatLng>  doInBackground(Void... voids) {
        String response;
        try{

           HttpdataHandler http = new HttpdataHandler();
            for(String address : Name) {
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", address,context.getResources().getString(R.string.MapKey));
                response = http.getHTTPData(url);
                JSONObject jsonObject = new JSONObject(response);
                double lat = (double) ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat");
                double lng = (double) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng");
                list.add(new LatLng(lat,lng));
            }

          List<Address> address;
          for( String name: Name)
          {
                address =geocoder.getFromLocationName(name,5);
              if (address  != null && address.size() > 0) {
                  list.add(new LatLng(address.get(0).getLatitude(),address.get(0).getLongitude()));
              }
          }
            return list;

        }
        catch (Exception ex)
        {

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngs) {
        this.getCoordinates.UpdatLatLng(latLngs);
        if(dialog.isShowing())
            dialog.dismiss();
    }



}
