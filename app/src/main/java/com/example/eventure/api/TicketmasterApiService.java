package com.example.eventure.api;

import com.example.eventure.model.EventResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TicketmasterApiService {
    
    @GET("discovery/v2/events.json")
    Call<EventResponse> searchEvents(
        @Query("apikey") String apiKey,
        @Query("latlong") String latLong,
        @Query("radius") String radius,
        @Query("city") String city,
        @Query("stateCode") String stateCode,
        @Query("countryCode") String countryCode,
        @Query("keyword") String keyword,
        @Query("size") Integer size,
        @Query("page") Integer page,
        @Query("sort") String sort
    );
}
