package com.darrienglasser.refugevolunteerservice;

public class HelpData {
    private String number;
    private String need;
    private String location;
    private String timeStamp;

    /**
     * Basic constructor. Left without arguments so Firebase can deserialze data.
     */
    public HelpData() {
    }

    /**
     * Debug constructor. Used to push dummy data to data members when unable to pull from
     * Firebase.
     *
     * @param number Refugee number.
     * @param need Required service.
     * @param location General location.
     * @param timeStamp When message was sent.
     */
    public HelpData(String number, String need, String location, String timeStamp) {
        this.number = number;
        this.need = need;
        this.location = location;
        this.timeStamp = timeStamp;
    }

    public String getNumber() {
        return number;
    }

    public String getNeed() {
        return need;
    }

    public String getLocation() {
        return location;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
