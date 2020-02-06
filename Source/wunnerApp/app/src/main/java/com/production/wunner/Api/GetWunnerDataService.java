package com.production.wunner.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.production.wunner.Model.Station;
import com.production.wunner.Model.Team;
import com.production.wunner.Model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetWunnerDataService {
    @POST("user/login")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json;charset=utf-8",
            "Cache-Control: max-age=640000"
    })
    Call<User> LoginUser(@Body UserLogin userLogin);
    @FormUrlEncoded
    @POST("user/login")
    Call<User> LoginUsers(@Field("userName") String name, @Field("userPassword")String password);

    @GET("team/get")
    Call<Team> GetTeam(@Query("teamID") String team);
    @GET("station/get")
    Call<Station> GetStation(@Query("stationID") String station);
    @GET("team/submit")
    Call<String>  SubmitTimer(@Query("teamID") String teamID);
    @GET("user/setlayout")
    Call<String>  UpdateLayout(@Query("userName") String userName, @Query("layoutType") String layoutType);
    @GET("team/start")
    Call<String> StartMission(@Query("teamID") String teamID, @Query("stationID") String station);
    @POST("station/mark")
    Call<String> SubmitPoint(@Query("teamID") String teamID, @Query("stationID") String stationID, @Query("point") String point);
    @GET("station/getwithusername")
    Call<ArrayList<String>> getStationbyUser(@Query("userName") String userName);



    public class UserLogin{
        @SerializedName("userName")
        @Expose
        private String  UserName;
        @SerializedName("userPassword")
        @Expose
        private String UserPass;

        public UserLogin(String UserName, String userPass) {
            this.UserName = UserName;
            this.UserPass = userPass;
        }
    }

}
