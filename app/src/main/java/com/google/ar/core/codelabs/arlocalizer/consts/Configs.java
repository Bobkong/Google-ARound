package com.google.ar.core.codelabs.arlocalizer.consts;

public interface Configs {
    // Azure speech
    String speechSubscriptionKey = "3c92dac0e64443f1bf51c236948ab803";
    String serviceRegion = "eastus";

    // network response
    int requestSuccess = 0;
    int requestFail = 1;

    // intent extra
    String drugDetailFromScene = "drugDetailFromScene";

    // localize mode
    int chat_mode = 0;
    int static_mode = 1;
}
