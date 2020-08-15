package com.example.piu_project;

public class UserDetail {
    private String level;
    private String achivement;
    private String youtubeLinkP;
    private String youtubeLinkJ;
    private String youtubeLinkN;
    private String unlockCondition;

    public UserDetail() {
        this.level = "";
        this.achivement = "";
        this.youtubeLinkP = "";
        this.youtubeLinkJ = "";
        this.youtubeLinkN = "";
        this.unlockCondition = "";
    }

    public UserDetail(String level, String achivement, String youtubeLinkP, String youtubeLinkJ, String youtubeLinkN, String unlockCondition) {
        this.level = level;
        this.achivement = achivement;
        this.youtubeLinkP = youtubeLinkP;
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

    public String getYoutubeLinkP() {
        return youtubeLinkP;
    }

    public void setYoutubeLinkP(String youtubeLinkP) {
        this.youtubeLinkP = youtubeLinkP;
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


