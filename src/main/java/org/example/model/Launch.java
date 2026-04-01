package org.example.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Launch {

    private String id;
    private String name;

    @SerializedName("flight_number")
    private int flightNumber;

    @SerializedName("date_utc")
    private String dateUtc;

    private Boolean success;
    private boolean upcoming;
    private String details;
    private List<Failure> failures;
    private List<Core> cores;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDateUtc() {
        return dateUtc;
    }

    public void setDateUtc(String dateUtc) {
        this.dateUtc = dateUtc;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public boolean isUpcoming() {
        return upcoming;
    }

    public void setUpcoming(boolean upcoming) {
        this.upcoming = upcoming;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public void setFailures(List<Failure> failures) {
        this.failures = failures;
    }

    public List<Core> getCores() {
        return cores;
    }

    public void setCores(List<Core> cores) {
        this.cores = cores;
    }
}
