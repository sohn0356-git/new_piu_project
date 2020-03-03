package com.example.piu_project;

public class AchievementInfo {
    private int n_Great;
    private int n_Good;
    private int n_Bad;
    private int n_Miss;
    private String photoUrl;

    public AchievementInfo(int n_Great, int n_Good, int n_Bad, int n_Miss, String photoUrl) {
        this.n_Great = n_Great;
        this.n_Good = n_Good;
        this.n_Bad = n_Bad;
        this.n_Miss = n_Miss;
        this.photoUrl = photoUrl;
    }

    public int getN_Great() {
        return n_Great;
    }

    public void setN_Great(int n_Great) {
        this.n_Great = n_Great;
    }

    public int getN_Good() {
        return n_Good;
    }

    public void setN_Good(int n_Good) {
        this.n_Good = n_Good;
    }

    public int getN_Bad() {
        return n_Bad;
    }

    public void setN_Bad(int n_Bad) {
        this.n_Bad = n_Bad;
    }

    public int getN_Miss() {
        return n_Miss;
    }

    public void setN_Miss(int n_Miss) {
        this.n_Miss = n_Miss;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
