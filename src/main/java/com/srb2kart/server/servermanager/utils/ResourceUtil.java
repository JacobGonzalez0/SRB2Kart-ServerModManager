package com.srb2kart.server.servermanager.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import com.srb2kart.server.servermanager.models.Kart;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import net.mtrop.doom.DoomPK3;
import net.mtrop.doom.WadBuffer;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.Colormap;
import net.mtrop.doom.graphics.PNGPicture;
import net.mtrop.doom.graphics.Palette;
import net.mtrop.doom.graphics.Picture;
import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.util.GraphicUtils;
import net.mtrop.doom.util.TextUtils;
import net.mtrop.doom.util.WadUtils;

@Component
public class ResourceUtil {

    public static String getBase64ProfileWad(String path) throws IOException{

        //Get Palette
        File palFile = new File("res/PLAYPAL");
        Palette pal = new Palette();
        pal.readBytes(new FileInputStream(palFile));

        //Get ColorMap
        File mapFile = new File("res/COLORMAP");
        Colormap map = new Colormap();
    
        map.readBytes(new FileInputStream(mapFile));

        WadBuffer file = new WadBuffer(new File(path));
        WadEntry tex = file.getEntry(file.getEntryCount()-2);
        
        Picture png = new Picture();
        png.readBytes(file.getInputStream(tex));
        PNGPicture p =  GraphicUtils.createPNGImage(png, pal, map);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        p.writeBytes(oStream);
        byte[] fileContent = oStream.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        
        return "data:image/png;base64, " + encodedString;
    }

    public static Kart getKartsWad(String path) throws IOException{

        Kart newKart = new Kart();

        //Get Palette
        File palFile = new File("res/PLAYPAL");
        Palette pal = new Palette();
        pal.readBytes(new FileInputStream(palFile));

        //Get ColorMap
        File mapFile = new File("res/COLORMAP");
        Colormap map = new Colormap();
    
        map.readBytes(new FileInputStream(mapFile));

        WadBuffer file = new WadBuffer(new File(path));
        WadEntry tex = file.getEntry(file.getEntryCount()-2);
        
        Picture png = new Picture();
        png.readBytes(file.getInputStream(tex));
        PNGPicture p =  GraphicUtils.createPNGImage(png, pal, map);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        p.writeBytes(oStream);
        byte[] fileContent = oStream.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        
        Iterator<WadEntry> it = file.iterator();
        
        while(it.hasNext()){
            WadEntry entry = it.next();
            if(entry.getName().equalsIgnoreCase("s_skin")){
                String unparsedData = new String(file.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
                
                //splits up the data to be parsed
                String[] stats = unparsedData.split("( = )|(\n)");

                newKart.setName(stats[1]);
                newKart.setRealname(stats[3]);
                newKart.setKartspeed(stats[11]);
                newKart.setKartweight(stats[13]);
                newKart.setStartcolor(stats[15]);
                newKart.setPrefcolor(stats[17]);
           
            }
            
        }
        newKart.setImage(encodedString);
        newKart.setFilename(path);

        return newKart;
    }

    public static List<Kart> getKartsPk3(String path) throws ZipException, IOException{

        List<Kart> exportKarts = new ArrayList<Kart>();

        //Get Palette
        File palFile = new File("res/PLAYPAL");
        Palette pal = new Palette();
        pal.readBytes(new FileInputStream(palFile));

        //Get ColorMap
        File mapFile = new File("res/COLORMAP");
        Colormap map = new Colormap();
        map.readBytes(new FileInputStream(mapFile));
        
        //get the file 
        DoomPK3 file = new DoomPK3(new File(path));

    
        //get all info from skins
        List<String> skins = file.getEntriesStartingWith("skin");
        List<String> fSkins = new ArrayList<String>();
        for(String skin : skins){
            if(skin.substring(skin.length()-4).equalsIgnoreCase("skin")){
                fSkins.add(skin);
            }

        }
        
        //get all the entries relating to graphics
        List<String> entries = file.getEntriesStartingWith("graphics");
        List<String> fEntries = new ArrayList<String>();
        for(String entry : entries){
            if(entry.substring(entry.length()-4).equalsIgnoreCase("want") && fEntries.size() < fSkins.size() ){
                fEntries.add(entry);
            }
        }

        //go though and make beans for all the data
        for(int i = 0; i < fEntries.size()-1; i++){

            Kart newKart = new Kart();
            
            Picture png = new Picture();

            //grab the third entry as its the profile picture
            png.readBytes(file.getInputStream(fEntries.get(i)));
    
            //using the default pallete, create a png
            PNGPicture p =  GraphicUtils.createPNGImage(png, pal, map);
    
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            p.writeBytes(oStream);
            byte[] fileContent = oStream.toByteArray();
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            newKart.setImage(encodedString);

            String unparsedData = file.getTextData(fSkins.get(i), TextUtils.ASCII);
            //splits up the data to be parsed
            String[] stats = unparsedData.split("( = )|(\n)");

            newKart.setName(stats[1]);
            newKart.setRealname(stats[3]);
            newKart.setKartspeed(stats[11]);
            newKart.setKartweight(stats[13]);
            newKart.setStartcolor(stats[15]);
            newKart.setPrefcolor(stats[17]);
            newKart.setFilename(path);
            newKart.setIndex(i);
            
            exportKarts.add(newKart);
        }
        
        //close the file
        file.close();

        return exportKarts;
    }

    public static void exportCharacter(Kart kart) throws ZipException, IOException{

        String fileExt = kart.getFilename().substring(kart.getFilename().length()-3);

        //TODO: Refactor because this was written at 6am
        if(fileExt.equalsIgnoreCase("pk3")){

            //get the file 
            DoomPK3 file = new DoomPK3(new File(kart.getFilename()));
            List<String> files = new ArrayList<String>();

            String realname = kart.getName();
            String sPrefix = realname.toLowerCase();
            System.out.println("hit" + sPrefix);
            System.out.println("hit2" + realname);

            //look for all related files and sort though them
            List<String> graphics = file.getEntriesStartingWith("graphics");
            List<String> skins = file.getEntriesStartingWith("skins");
            List<String> sounds = file.getEntriesStartingWith("sounds");

            String soundsPath = new String();
            for(String filename: sounds){
                if(filename.toLowerCase().contains(sPrefix)){
                    System.out.println(filename);
                    soundsPath = filename;
                }
            }
            int soundLength;
            StringBuilder soundFile = new StringBuilder();
            for(int i=soundsPath.length(); 0>soundsPath.length();i--){
                soundFile.append(soundsPath.charAt(i));
            }
            soundLength = soundFile.indexOf("");
            System.out.println(soundsPath);
            System.out.println(soundFile);
            System.out.println(soundLength);

            soundsPath = soundsPath.substring(0,soundsPath.length()-soundLength);

            for(String filename: sounds){
                if(filename.contains(soundsPath)){
                    files.add(filename);
                }
            }

            String skinPath = new String();
            for(String filename: skins){
                if(filename.toLowerCase().contains(sPrefix) && !filename.contains("S_SKIN")){
                    skinPath = filename;
                    break;
                }
            }

            //Add skin def
            System.out.println("DD: " + skinPath);
            skinPath = skinPath.substring(0,skinPath.length()-6);
            files.add(skinPath + "S_SKIN");
            for(String filename: skins){
                if(filename.contains(skinPath) && !filename.contains("S_SKIN")){
                    files.add(filename);
                }
            }

            String wantStart = new String();
            for(String filename: graphics){
                
                if(filename.toLowerCase().contains(sPrefix) && !filename.contains("S_SKIN")){
                    wantStart = filename;
                    break;
                }

            }

            String wantFix = wantStart.substring(0, wantStart.length()-4);
            wantFix = wantFix.substring(wantStart.length()-8);

            String correctPath = wantStart.substring(0,wantStart.length()-8);
            files.add(correctPath + wantFix.toUpperCase() +"RANK");
            files.add(correctPath + wantFix.toUpperCase() +"WANT");
            files.add(correctPath + wantFix.toUpperCase() +"MMAP");
            
            //prepare new wad file
            WadFile newwad = WadFile.createWadFile("res/" + realname + ".wad");
            
            for(int i = 0; i < files.size(); i++){

                ZipEntry entry = file.getEntry(files.get(i));
                System.out.println(files.get(i));
                byte[] bArray = file.getData(files.get(i));
                String nam = new String();

                try{
                    nam = new File(entry.getName()).getName();
                }catch(NullPointerException ex){
                    System.out.println("missing .lmp");

                    entry = file.getEntry(files.get(i) + ".lmp");
                    bArray = file.getData(files.get(i) + ".lmp");
                    nam = new File(entry.getName()).getName(); 
                    
                }
                
                if(nam.length() > 8){
                    nam = nam.substring(0,8);
                }
                
                newwad.addData(nam, bArray);
         
            }

            newwad.close();
        }

        if(fileExt.equalsIgnoreCase("wad")){

        }

    }
}
