package com.darrienglasser.refugevolunteerservice;

public class HelpData {
    private String number;
    private String need;
    private String location;
    private String timeStamp;

    public HelpData() {
        // Empty, necessary for Firebase to deserialze data
    }

    // DEBUG
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
