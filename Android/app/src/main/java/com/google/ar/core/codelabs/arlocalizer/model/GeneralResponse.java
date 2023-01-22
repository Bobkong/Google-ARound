package com.google.ar.core.codelabs.arlocalizer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeneralResponse implements Serializable {
    @SerializedName("success")
    boolean success;

    @SerializedName("err")
    String err;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
