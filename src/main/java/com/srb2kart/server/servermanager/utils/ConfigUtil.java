package com.srb2kart.server.servermanager.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

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
        

    }

    
    

}
