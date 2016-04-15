package com.keepfit.triggers.utils.enums;

/**
 * Created by Edward on 4/14/2016.
 */
public enum Scenario {
    FIRST(1, "first"),
    SECOND(2, "second"),
    THIRD(3, "third");

    public int id;
    public String title;

    Scenario(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Scenario getById(int id) {
        for (Scenario scenario : Scenario.values())
            if (id == scenario.id)
                return scenario;
        return null;
    }
}
