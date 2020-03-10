package com.example.piu_project;

public class UserDetail {
    private String level;
    private String achivement;
    private String youtubeLinkE;
    private String youtubeLinkJ;
    private String youtubeLinkN;
    private String unlockCondition;

    public UserDetail() {
        this.level = "";
        this.achivement = "";
        this.youtubeLinkE = "";
        this.youtubeLinkJ = "";
        this.youtubeLinkN = "";
        this.unlockCondition = "";
    }

    public UserDetail(String level, String achivement, String youtubeLinkE, String youtubeLinkJ, String youtubeLinkN, String unlockCondition) {
        this.level = level;
        this.achivement = achivement;
        this.youtubeLinkE = youtubeLinkE;
        this.youtubeLinkJ = youtubeLinkJ;
        this.youtubeLinkN = youtubeLinkN;
        this.unlockCondition = unlockCondition;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAchivement() {
        return achivement;
    }

    public void setAchivement(String achivement) {
        this.achivement = achivement;
    }

    public String getYoutubeLinkE() {
        return youtubeLinkE;
    }

    public void setYoutubeLinkE(String youtubeLinkE) {
        this.youtubeLinkE = youtubeLinkE;
    }

    public String getYoutubeLinkJ() {
        return youtubeLinkJ;
    }

    public void setYoutubeLinkJ(String youtubeLinkJ) {
        this.youtubeLinkJ = youtubeLinkJ;
    }

    public String getYoutubeLinkN() {
        return youtubeLinkN;
    }

    public void setYoutubeLinkN(String youtubeLinkN) {
        this.youtubeLinkN = youtubeLinkN;
    }

    public String getunlockCondition() {
        return unlockCondition;
    }

    public void setunlockCondition(String unlockCondition) {
        this.unlockCondition = unlockCondition;
    }
}


