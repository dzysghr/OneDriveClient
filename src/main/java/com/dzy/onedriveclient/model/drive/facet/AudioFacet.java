package com.dzy.onedriveclient.model.drive.facet;

/**
 * Created by dzysg on 2017/5/11 0011.
 */

public class AudioFacet {


    /**
     * album : string
     * albumArtist : string
     * artist : string
     * bitrate : 128
     * composers : string
     * copyright : string
     * disc : 0
     * discCount : 0
     * duration : 567
     * genre : string
     * hasDrm : false
     * isVariableBitrate : false
     * title : string
     * track : 1
     * trackCount : 16
     * year : 2014
     */

    private String album;
    private String albumArtist;
    private String artist;
    private int bitrate;
    private String composers;
    private String copyright;
    private int disc;
    private int discCount;
    private int duration;
    private String genre;
    private boolean hasDrm;
    private boolean isVariableBitrate;
    private String title;
    private int track;
    private int trackCount;
    private int year;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getComposers() {
        return composers;
    }

    public void setComposers(String composers) {
        this.composers = composers;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public int getDisc() {
        return disc;
    }

    public void setDisc(int disc) {
        this.disc = disc;
    }

    public int getDiscCount() {
        return discCount;
    }

    public void setDiscCount(int discCount) {
        this.discCount = discCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isHasDrm() {
        return hasDrm;
    }

    public void setHasDrm(boolean hasDrm) {
        this.hasDrm = hasDrm;
    }

    public boolean isIsVariableBitrate() {
        return isVariableBitrate;
    }

    public void setIsVariableBitrate(boolean isVariableBitrate) {
        this.isVariableBitrate = isVariableBitrate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
