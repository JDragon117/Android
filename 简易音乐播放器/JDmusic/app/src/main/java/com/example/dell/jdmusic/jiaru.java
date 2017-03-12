package com.example.dell.jdmusic;

/**
 * Created by dell on 2016/12/19.
 */

public class jiaru {
    private String aName;
    private String aSpeak;
    private String aPath;

    public jiaru(){}
    public jiaru(String aName, String aSpeak) {
        this.aName = aName;
        this.aSpeak = aSpeak;
    }
    public String getaName() {
        return aName;
    }

    public String getaSpeak() {
        return aSpeak;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaSpeak(String aSpeak) {
        this.aSpeak = aSpeak;
    }

    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }
}
