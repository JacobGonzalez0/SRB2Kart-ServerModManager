package com.srb2kart.server.servermanager.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
public class Kart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String realname;
    private String kartspeed;
    private String kartweight;
    private String startcolor;
    private String prefcolor;
    private String image;
    private String filename;
    private int index;

    public Kart(long id, String name, String realname, String kartspeed, String kartweight, String startcolor, String prefcolor) {
        this.id = id;
        this.name = name;
        this.realname = realname;
        this.kartspeed = kartspeed;
        this.kartweight = kartweight;
        this.startcolor = startcolor;
        this.prefcolor = prefcolor;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImage() {
        return "data:image/png;base64, " + this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Kart() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getKartspeed() {
        return this.kartspeed;
    }

    public void setKartspeed(String kartspeed) {
        this.kartspeed = kartspeed;
    }

    public String getKartweight() {
        return this.kartweight;
    }

    public void setKartweight(String kartweight) {
        this.kartweight = kartweight;
    }

    public String getStartcolor() {
        return this.startcolor;
    }

    public void setStartcolor(String startcolor) {
        this.startcolor = startcolor;
    }

    public String getPrefcolor() {
        return this.prefcolor;
    }

    public void setPrefcolor(String prefcolor) {
        this.prefcolor = prefcolor;
    }

}
