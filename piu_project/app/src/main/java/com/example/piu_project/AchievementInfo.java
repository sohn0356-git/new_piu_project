package com.example.piu_project;

public class AchievementInfo {
    private String songInfo;
    private String achievement;
    private String n_Great;
    private String n_Good;
    private String n_Bad;
    private String n_Miss;
    private String photoUrl;
    private String user_id;

    public AchievementInfo(String songInfo, String achievement, String n_Great, String n_Good, String n_Bad, String n_Miss, String photoUrl, String user_id) {
        this.songInfo = songInfo;
        this.achievement = achievement;
        this.n_Great = n_Great;
        this.n_Good = n_Good;
        this.n_Bad = n_Bad;
        this.n_Miss = n_Miss;
        this.photoUrl = photoUrl;
        this.user_id = user_id;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(String songInfo) {
        this.songInfo = songInfo;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getN_Great() {
        return n_Great;
    }

    public void setN_Great(String n_Great) {
        this.n_Great = n_Great;
    }

    public String getN_Good() {
        return n_Good;
    }

    public void setN_Good(String n_Good) {
        this.n_Good = n_Good;
    }

    public String getN_Bad() {
        return n_Bad;
    }

    public void setN_Bad(String n_Bad) {
        this.n_Bad = n_Bad;
    }

    public String getN_Miss() {
        return n_Miss;
    }

    public void setN_Miss(String n_Miss) {
        this.n_Miss = n_Miss;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
