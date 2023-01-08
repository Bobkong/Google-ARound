package com.google.ar.core.codelabs.arlocalizer.netservice.Api;



import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
import com.google.ar.core.codelabs.arlocalizer.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignApi {
    //sign up
    @POST("/users/signup")
    Observable<GeneralResponse> signUp(
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

}
