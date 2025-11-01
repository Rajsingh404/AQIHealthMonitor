package com.example.aqihealthmonitor.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AQIApiService {

    @GET("data/2.5/air_pollution")
    Call<AirPollutionResponse> getAirPollution(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String appid
    );

    @GET("geo/1.0/direct")
    Call<List<GeocodingResponse>> searchCity(
            @Query("q") String q,
            @Query("limit") int limit,
            @Query("appid") String appid
    );

    class AirPollutionResponse {
        public List<AirQuality> list;

        public static class AirQuality {

            public Components components;

            public static class Components {
                public double co;
                public double no2;
                public double o3;
                public double so2;
                public double pm2_5;
                public double pm10;
            }
        }
    }

    class GeocodingResponse {
        public String name;
        public String country;
        public double lat;
        public double lon;
    }
}
