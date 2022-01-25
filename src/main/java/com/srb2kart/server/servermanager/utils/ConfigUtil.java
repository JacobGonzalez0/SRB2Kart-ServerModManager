package com.srb2kart.server.servermanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.srb2kart.server.servermanager.models.ConfigEntry;
import com.srb2kart.server.servermanager.repositories.ConfigEntryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtil {

    private static
    ConfigEntryRepository configRepo;
    
    private static File config;

    @Autowired
    public ConfigUtil(ConfigEntryRepository configRepo) {
        ConfigUtil.configRepo = configRepo;
    }

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

    public static List<ConfigEntry> readConfig() throws Exception{

        prepareConfig();

        byte[] encoded = Files.readAllBytes(Paths.get(config.getAbsolutePath()));
        String configString = new String(encoded, StandardCharsets.UTF_8);
        String[] rawEntries = configString.split("(\n| )");

        List<ConfigEntry> configEntries = new ArrayList<ConfigEntry>();

        System.out.println(rawEntries.length);
        for(int i = 0; i < rawEntries.length; i++){
            if(i % 2 == 0){
                configEntries.add(parseEntry(rawEntries[i], rawEntries[i+1]));
            }
        }

        return configEntries;

    }

    public static void readConfigSave() throws Exception{

        prepareConfig();

        byte[] encoded = Files.readAllBytes(Paths.get(config.getAbsolutePath()));
        String configString = new String(encoded, StandardCharsets.UTF_8);
        String[] rawEntries = configString.split("(\n| )");

        List<ConfigEntry> configEntries = new ArrayList<ConfigEntry>();

        System.out.println(rawEntries.length);
        for(int i = 0; i < rawEntries.length; i++){
            if(i % 2 == 0){
                configEntries.add(parseEntry(rawEntries[i], rawEntries[i+1]));
            }
        }

        for(ConfigEntry entry: configEntries){
            System.out.println(entry.getRawCommand());
            System.out.println(entry.getId());
            configRepo.save(entry);
        }


    }

    public static void clearConfig() throws IOException {
        FileWriter fwOb = new FileWriter(config, false); 
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    public static void writeConfig() throws Exception{

        prepareConfig();
        List<ConfigEntry> configEntries = configRepo.findAll();
        clearConfig();
        FileWriter myWriter = new FileWriter(config, true);
        for(ConfigEntry entry: configEntries){
            myWriter.write(entry.getRawCommand() + "\n");
        }
        myWriter.close();

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
        newEntry.setRawCommand(entry + " " + arg);

        return newEntry;
    }
    
    

}
