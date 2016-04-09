package com.keepfit.triggers.utils.enums;

/**
 * Created by Dino on 09/04/2016.
 */
public class KeepFitCalendarEvent {
    private String eventName;
    private String startTime;
    private String endTime;

    public KeepFitCalendarEvent(String name, String start, String end){
        this.eventName = name;
        this.startTime = start;
        this.endTime = end;
    }

    public String getName(){return this.eventName;}
    public String getStart(){return this.startTime;}
    public String getEnd(){return this.endTime;}
}
