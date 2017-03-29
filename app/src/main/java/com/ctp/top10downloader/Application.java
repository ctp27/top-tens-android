package com.ctp.top10downloader;

/**
 * Created by CTP on 12/28/2016.
 */

public class Application {
    private String name;
    private String artist;
    private String releasDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleasDate() {
        return releasDate;
    }

    public void setReleasDate(String releasDate) {
        this.releasDate = releasDate;
    }

    @Override
    public String toString() {
        return "Name: "+getName() + "\n"+
                "Artist: "+getArtist()+"\n"+
                "Release Date: "+getReleasDate();
    }
}
