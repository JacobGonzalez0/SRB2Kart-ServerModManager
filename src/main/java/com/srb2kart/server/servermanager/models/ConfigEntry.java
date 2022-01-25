package com.srb2kart.server.servermanager.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfigEntry {
    @Id
    private long id;
    private String description;
    private String catagory;
    private String rawCommand;


    public ConfigEntry() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatagory() {
        return this.catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getRawCommand() {
        return this.rawCommand;
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }

}
