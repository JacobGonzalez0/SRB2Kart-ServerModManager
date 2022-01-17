package com.srb2kart.server.servermanager.utils;

import com.srb2kart.server.servermanager.models.Kart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class LoaderUtil {


    private static List<Kart> karts;
    
    @PostConstruct 
    public static void loadAllAssets() throws Exception{
        karts = new ArrayList<Kart>();

        //get all files in the directory
        String[] pathnames;
        File directory = new File("res/karts");
        pathnames = directory.list();

        try{   

            for (String pathname : pathnames) {

                Kart newKart = new Kart();
                String ext = pathname.substring(pathname.length()-3);
                
                //check filetype and extract resources needed
                if(ext.equalsIgnoreCase("pk3")){ 
                    
                    List<Kart> extractedResources = (ResourceUtil.getKartsPk3("res/karts/" + pathname));
                    karts.addAll(extractedResources);
                }

                if(ext.equalsIgnoreCase("wad")){

                    newKart = (ResourceUtil.getKartsWad("res/karts/" + pathname));
                    karts.add(newKart);
                }

            }

            

            
        }catch(Exception ex){
            throw ex;
        }  
    }
    
  

    public static List<Kart> getAllKarts(){
        return karts;
    }
}
