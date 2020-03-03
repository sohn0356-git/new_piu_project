package com.example.piu_project;

public class SongInfo {
    private String album;
    private String artist;
    private String bpm;
    private String level;
    private String title;
    private String category;



    public SongInfo(String album, String artist, String bpm, String level, String title, String category) {
        this.album = album;
        this.artist = artist;
        this.bpm = bpm;
        this.level = level;
        this.title = title;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getBpm() {
        return bpm;
    }

    public String getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
