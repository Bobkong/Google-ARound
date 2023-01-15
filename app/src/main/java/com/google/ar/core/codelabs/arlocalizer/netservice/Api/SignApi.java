package com.google.ar.core.codelabs.arlocalizer.netservice.Api;



import com.google.ar.core.codelabs.arlocalizer.model.CloudAnchor;
import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
import com.google.ar.core.codelabs.arlocalizer.model.NavigateResponse;
import com.google.ar.core.codelabs.arlocalizer.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignApi {
    //sign up
    @POST("/users/signout")
    Observable<GeneralResponse> signOut(
            @Body User params
            );


    //sign in
    @GET("/users/login")
    Observable<GeneralResponse> signIn(
            @Query("username")
            String username,
            @Query("password")
            String password
            );

    //navigation
    @POST("/users/navigate")
    Observable<NavigateResponse> navigate(
            @Body CloudAnchor params
    );

    //stop navigation
    @POST("/users/stopnavigate")
    Observable<GeneralResponse> stopNavigate(
            @Body User params
    );

}
