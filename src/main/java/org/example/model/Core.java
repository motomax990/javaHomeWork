package org.example.model;

import com.google.gson.annotations.SerializedName;

public class Core {

    private String core;
    private Integer flight;
    private Boolean gridfins;
    private Boolean legs;
    private Boolean reused;

    @SerializedName("landing_attempt")
    private Boolean landingAttempt;

    @SerializedName("landing_success")
    private Boolean landingSuccess;

    @SerializedName("landing_type")
    private String landingType;

    private String landpad;

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }

    public Integer getFlight() {
        return flight;
    }

    public void setFlight(Integer flight) {
        this.flight = flight;
    }

    public Boolean getGridfins() {
        return gridfins;
    }

    public void setGridfins(Boolean gridfins) {
        this.gridfins = gridfins;
    }

    public Boolean getLegs() {
        return legs;
    }

    public void setLegs(Boolean legs) {
        this.legs = legs;
    }

    public Boolean getReused() {
        return reused;
    }

    public void setReused(Boolean reused) {
        this.reused = reused;
    }

    public Boolean getLandingAttempt() {
        return landingAttempt;
    }

    public void setLandingAttempt(Boolean landingAttempt) {
        this.landingAttempt = landingAttempt;
    }

    public Boolean getLandingSuccess() {
        return landingSuccess;
    }

    public void setLandingSuccess(Boolean landingSuccess) {
        this.landingSuccess = landingSuccess;
    }

    public String getLandingType() {
        return landingType;
    }

    public void setLandingType(String landingType) {
        this.landingType = landingType;
    }

    public String getLandpad() {
        return landpad;
    }

    public void setLandpad(String landpad) {
        this.landpad = landpad;
    }
}
