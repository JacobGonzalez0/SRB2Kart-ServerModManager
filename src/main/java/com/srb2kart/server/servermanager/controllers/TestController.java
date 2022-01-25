package com.srb2kart.server.servermanager.controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import com.srb2kart.server.servermanager.utils.ConfigUtil;
import com.srb2kart.server.servermanager.utils.JsonUtil;
import com.srb2kart.server.servermanager.utils.LoaderUtil;
import com.srb2kart.server.servermanager.utils.ResourceUtil;
import com.srb2kart.server.servermanager.utils.ZipUtil;
import com.srb2kart.services.LogServices;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.minidev.json.JSONObject;
import net.mtrop.doom.DoomPK3;
import net.mtrop.doom.Wad;
import net.mtrop.doom.WadBuffer;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.Colormap;
import net.mtrop.doom.graphics.PNGPicture;
import net.mtrop.doom.graphics.Palette;
import net.mtrop.doom.graphics.Picture;
import net.mtrop.doom.util.GraphicUtils;


@Controller
public class TestController {

    Process process;
    LogServices log;

    @GetMapping("/read")
    public String read(Model model) throws Exception{
        ConfigUtil.readConfig();
        return "index";
    }

    @GetMapping("/write")
    public String write(Model model){
        try {
            ConfigUtil.writeConfig();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "index";
    }

    @GetMapping("/run")
    public String run(Model model){
        String status = new String();
        if(process != null && process.isAlive()){
            status = "Running";
        }else{
            status = "Stopped";
        }

        model.addAttribute("status", status);
        return "run";
    }
    
    @PostMapping("/run")
    public String runAction(
        Model model,
        @RequestParam(value="action") String action
    ) throws IOException{

        System.out.println(action);

        //check if config exists
   

        if(action.equals("run")){
            ProcessBuilder builder = new ProcessBuilder("res/server/srb2kart.exe", "-console", "-dedicated", "-config server.cfg");
            
            log = new LogServices();
            log.start();
            
        
            
            builder.redirectErrorStream(true);
    
            process = builder.start();
            
    
        }else if(action.equals("stop")){
            log.stop();
            process.destroy();
        }
        
        String status = new String();
        if(process.isAlive()){
            status = "Running";
        }else{
            status = "Stopped";
        }
        model.addAttribute("status", status);
        return "run";
    }

    @GetMapping("/install")
    public String install(Model model){

        return "install";
    }

    @PostMapping("/install")

    public String installAction(Model model) {
        try{
            System.out.println("Starting Download");
            //grab the latest release url
            org.json.JSONArray array = JsonUtil.readJsonArrayFromUrl("https://api.github.com/repos/STJr/Kart-Public/releases");
            org.json.JSONObject root = array.getJSONObject(0);
            org.json.JSONArray assets = root.getJSONArray("assets");
            String url = new String();
            String filename = new String();
            for(int i = 0;i<assets.length();i++){
                filename = assets.getJSONObject(i).getString("name");
                if(filename.substring(filename.length()-13,filename.length()).equals("Installer.exe")){
                    filename = assets.getJSONObject(i).getString("name");
                    url = assets.getJSONObject(i).getString("browser_download_url");
                    break;
                }
                
            }
            
            try (
                BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream("res/server/" + filename)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // handle exception
            }finally{
                System.out.println("Finished Download");
                String fileZip = "res/server/" + filename;
                String destDir = "res/server/";
                try{
                    System.out.println("Unzipping");
                    ZipUtil.unzip(fileZip, destDir);
                }catch(Exception ex){
                    //TODO: Write exception handling
                    System.out.println(ex.toString());
                }finally{
                    System.out.println("Unzip complete");
                }
                
            }
            
            
            System.out.println();
        }catch(Exception ex){
            System.out.println(ex.toString());;
        }
        
        return "install";
    }

    @GetMapping("/palette") 
    public String palette(Model model) throws FileNotFoundException, IOException{

        //Get ColorMap

        Path path = Paths.get("res/COLORMAP");
        byte[] data = Files.readAllBytes(path);

        
        Colormap map = new Colormap();
        map.readBytes(new ByteArrayInputStream(data));
       
        

        model.addAttribute("results", map);
        return "index";
    }
    

    @GetMapping("/kart/export")
    public String testing(Model model) throws ZipException, IOException{
        
        model.addAttribute("karts", LoaderUtil.getAllKarts());
        
        return "test";
    }

    @PostMapping("/kart/export/")
    public String kartCompile(
        Model model,
        @PathVariable int id) throws ZipException, IOException{
        System.out.println(LoaderUtil.getAllKarts().size());
        
        try{
            ResourceUtil.exportCharacter(LoaderUtil.getAllKarts().get(id));
        }catch(Exception ex){
            model.addAttribute("error", "Error exporting character");
            model.addAttribute("karts", LoaderUtil.getAllKarts());
            return "test";
        }
        
        model.addAttribute("message", "Success exporting " + LoaderUtil.getAllKarts().get(id).getRealname());
        model.addAttribute("karts", LoaderUtil.getAllKarts());
        
        return "test";
    }
}
