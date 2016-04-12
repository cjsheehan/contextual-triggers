package com.keepfit.triggers.interests;

import java.io.Serializable;

/**
 * Created by Chris on 12/04/2016.
 */
public class Category  implements Serializable {
    String id;
    String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category(String id, String title) {

        this.id = id;
        this.title = title;
    }
}