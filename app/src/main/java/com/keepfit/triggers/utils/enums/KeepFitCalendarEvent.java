package com.keepfit.triggers.utils.enums;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Dino on 09/04/2016.
 */
public class KeepFitCalendarEvent implements Serializable {

    private String eventName;
    private Date startTime;
    private Date endTime;

    public KeepFitCalendarEvent(String name, Date start, Date end){
        this.eventName = name;
        this.startTime = start;
        this.endTime = end;
    }

    public String getName(){return this.eventName;}
    public Date getStart(){return this.startTime;}
    public Date getEnd(){return this.endTime;}
}
