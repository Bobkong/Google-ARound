package com.google.ar.core.codelabs.arlocalizer.netservice.Api;



import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
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

    public Observable<GeneralResponse> signUp(String username, String password){
        return signApi.signUp(new User(username, password))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> signIn(String username, String password){
        return signApi.signIn(username, password)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
