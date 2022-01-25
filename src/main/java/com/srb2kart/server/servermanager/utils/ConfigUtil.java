package com.srb2kart.server.servermanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

import com.srb2kart.server.servermanager.models.ConfigEntry;

import org.springframework.stereotype.Component;

@Component
public class ConfigUtil {
    

    private static File config;

    @PostConstruct
    private static void prepareConfig() throws Exception{

        Path path = Paths.get("res/server/");
        config = new File("res/server/server.cfg");
        boolean exists = config.exists();
        boolean directoryExists = Files.isDirectory(path);

        if(directoryExists){

            if(!exists){

                config.createNewFile();

            }

        }

       
    }

    public static void readConfig() throws Exception{

        prepareConfig();

        FileInputStream fileInputStream = null;
        byte[] configBuffer = new byte[(int) config.length()];

        //read config into buffer
        try{
           //convert file into array of bytes
           fileInputStream = new FileInputStream(config);
           fileInputStream.read(configBuffer);
           fileInputStream.close();
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }

        String configString = Base64.getEncoder().encodeToString(configBuffer);
        String[] rawEntries = configString.split("\n");

        List<ConfigEntry> configEntries = new ArrayList<ConfigEntry>();

        for(int i = 0; i < rawEntries.length; i++){
            if(i % 2 != 0){
                configEntries.set(i, parseEntry(rawEntries[i], rawEntries[i+1]));
            }
        }

        
        


    }

    public static void writeConfig() throws Exception{

        prepareConfig();
        


    }

    private static ConfigEntry parseEntry(String entry, String arg){
        ConfigEntry newEntry = new ConfigEntry();

        switch(entry){
            case "addFile": newEntry.setCatagory("File");
                break;
            case "map": newEntry.setCatagory("Map");
                break;
            case "gametype": newEntry.setCatagory("GameType");
                break;
            case "motd": newEntry.setCatagory("Motd");
                break;
            case "password": newEntry.setCatagory("Motd");
                break;
        }

        newEntry.setArguement(arg);
        newEntry.setRawCommand(entry + " " + "arg");

        return newEntry;
    }
    
    

}
