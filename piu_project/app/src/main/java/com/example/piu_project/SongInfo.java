package com.example.piu_project;

import java.util.HashMap;

public class SongInfo {
    private int song_id;
    private String artist;
    private String title;
    private String level;
    private String bpm;
    private String category;
    private String version;
    private String userLevel;
    private String difficulty;
    private HashMap<String, HashMap<String,String>> stepmaker;
    private HashMap<String, HashMap<String,String>> youtubeLink;
    private HashMap<String, HashMap<String,String>> detailDifficulty;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getUserLevel() {
        if(userLevel==null || userLevel.equals("")){
            return -1;
        }else {
            return Integer.parseInt(userLevel);
        }
    }

    public SongInfo(String song_id, String artist, String title, String level, String bpm, String category, String version, HashMap<String, HashMap<String, String>> stepmaker, HashMap<String, HashMap<String, String>> youtubeLink,HashMap<String, HashMap<String, String>> detailDifficulty) {
        this.song_id = Integer.parseInt(song_id);
        this.artist = artist;
        this.title = title;
        this.level = level;
        this.bpm = bpm;
        this.category = category;
        this.version = version;
        this.userLevel = "";
        this.stepmaker = stepmaker;
        this.youtubeLink = youtubeLink;
        this.detailDifficulty = detailDifficulty;
    }
    public SongInfo(String song_id, String artist, String title, String level, String bpm, String category, String version, HashMap<String, HashMap<String, String>> stepmaker, HashMap<String, HashMap<String, String>> youtubeLink) {
        this.song_id = Integer.parseInt(song_id);
        this.artist = artist;
        this.title = title;
        this.level = level;
        this.bpm = bpm;
        this.category = category;
        this.version = version;
        this.userLevel = "";
        this.stepmaker = stepmaker;
        this.youtubeLink = youtubeLink;
    }

    public SongInfo(String difficulty) {
        this.title = "blank";
        this.difficulty = difficulty;
    }

    public SongInfo(String song_id, String artist, String title, String level, String bpm, String category, String version, HashMap<String, HashMap<String, String>> stepmaker, HashMap<String, HashMap<String, String>> youtubeLink, String difficulty) {
        this.song_id = Integer.parseInt(song_id);
        this.artist = artist;
        this.title = title;
        this.level = level;
        this.bpm = bpm;
        this.category = category;
        this.version = version;
        this.userLevel = "";
        this.stepmaker = stepmaker;
        this.youtubeLink = youtubeLink;
        this.difficulty = difficulty;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public HashMap<String, HashMap<String, String>> getYoutubeLink() {
        return youtubeLink;
    }

    public HashMap<String, HashMap<String, String>> getDetailDifficulty() {
        return detailDifficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setDetailDifficulty(HashMap<String, HashMap<String, String>> detailDifficulty) {
        this.detailDifficulty = detailDifficulty;
    }

    public void setYoutubeLink(HashMap<String, HashMap<String, String>> youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public HashMap<String, HashMap<String, String>> getStepmaker() {
        return stepmaker;
    }

    public void setStepmaker(HashMap<String, HashMap<String, String>> stepmaker) {
        this.stepmaker = stepmaker;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
