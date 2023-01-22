package com.google.ar.core.codelabs.arlocalizer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NavigateResponse implements Serializable {
    @SerializedName("success")
    boolean success;

    @SerializedName("cloudAnchor")
    CloudAnchor cloudAnchor;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CloudAnchor getCloudAnchor() {
        return cloudAnchor;
    }

    public void setCloudAnchor(CloudAnchor cloudAnchor) {
        this.cloudAnchor = cloudAnchor;
    }
}
