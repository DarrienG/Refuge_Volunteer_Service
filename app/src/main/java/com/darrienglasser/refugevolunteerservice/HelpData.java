package com.darrienglasser.refugevolunteerservice;

public class HelpData {
    private String loc;
    private String sender;
    private long time;
    private String type;


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
     * @param type Required service.
     * @param location General location.
     * @param timeStamp When message was sent.
     */
    public HelpData(String number, String type, String location, long timeStamp) {
        this.loc = number;
        this.type = type;
        this.sender = location;
        this.time = timeStamp;
    }

    public String getNumber() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return loc;
    }

    public long getTimeStamp() {
        return time;
    }


}
