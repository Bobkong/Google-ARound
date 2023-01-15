package com.google.ar.core.codelabs.arlocalizer.netservice.Api;



import com.google.ar.core.codelabs.arlocalizer.model.CloudAnchor;
import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
import com.google.ar.core.codelabs.arlocalizer.model.NavigateResponse;
import com.google.ar.core.codelabs.arlocalizer.model.User;
import com.google.ar.core.codelabs.arlocalizer.netservice.ARoundServiceManager;
import com.google.ar.core.codelabs.arlocalizer.netservice.HttpResultFunc;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SignService {
    private static SignService instance;
    public static synchronized SignService getInstance(){
        if(instance==null)
            instance=new SignService();
        return instance;
    }

    private final SignApi signApi= ARoundServiceManager.getInstance().create(SignApi.class);

    public Observable<GeneralResponse> signOut(String username){
        return signApi.signOut(new User(username, ""))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> signIn(String username, String password){
        return signApi.signIn(username, password)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<NavigateResponse> navigate(String username, double latitude, double longitude, double altitude){
        return signApi.navigate(new CloudAnchor(username, latitude, longitude, altitude))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> stopNavigate(String username){
        return signApi.stopNavigate(new User(username, ""))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
