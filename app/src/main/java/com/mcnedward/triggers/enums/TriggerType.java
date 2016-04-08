package com.mcnedward.triggers.enums;

/**
 * Created by Edward on 4/8/2016.
 */
public enum TriggerType {

    FIRST(1);

    int id;
    TriggerType(int id) {
        this.id = id;
    }

    public static TriggerType getById(int id) {
        for (TriggerType t : values()) {
            if (t.id == id)
                return t;
        }
        return null;
    }

}
