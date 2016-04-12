package com.keepfit.triggers.interests;

import java.io.Serializable;

/**
 * Created by Chris on 12/04/2016.
 */
public class Item implements Serializable {
    String[] position;
    String distance;
    String title;
    Category category;

    public Item(Category category, String distance, String[] position, String title) {
        this.category = category;
        this.distance = distance;
        this.position = position;
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String[] getPosition() {
        return position;
    }

    public void setPosition(String[] position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}