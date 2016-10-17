package com.bachelor.degree.travel.app;

import java.util.HashMap;

/**
 * Created by Andreea on 8/22/2016.
 */
public class OpenItem {
    private String isOpen;
    HashMap<String, String> days;

    public OpenItem() {
        this.isOpen = "";
        days = new HashMap<String, String>();
    }

    public void setOpen(String open) {
        this.isOpen = open;
    }

    public void setDays(HashMap<String, String> hashMap) {
        this.days.put("Monday", hashMap.get("Monday"));
        this.days.put("Tuesday", hashMap.get("Tuesday"));
        this.days.put("Wednesday", hashMap.get("Wednesday"));
        this.days.put("Thursday", hashMap.get("Thursday"));
        this.days.put("Friday", hashMap.get("Friday"));
        this.days.put("Saturday", hashMap.get("Saturday"));
        this.days.put("Sunday", hashMap.get("Sunday"));
    }

    public String getIsOpen() {
        return this.isOpen;
    }

    public HashMap<String, String> getDays() {
        return this.days;
    }
}
