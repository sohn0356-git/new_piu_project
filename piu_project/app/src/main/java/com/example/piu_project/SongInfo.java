package com.example.piu_project;

public class SongInfo {
    private String album;
    private String artist;
    private String bpm;
    private String level;
    private String title;

    public SongInfo(String album, String artist, String bpm, String level, String title) {
        this.album = album;
        this.artist = artist;
        this.bpm = bpm;
        this.level = level;
        this.title = title;
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
