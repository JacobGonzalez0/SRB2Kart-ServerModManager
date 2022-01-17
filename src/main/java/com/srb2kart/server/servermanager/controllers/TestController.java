package com.srb2kart.server.servermanager.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipException;

import javax.imageio.ImageIO;

import com.srb2kart.server.servermanager.utils.LoaderUtil;
import com.srb2kart.server.servermanager.utils.ResourceUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    

    @GetMapping("/test")
    public String testing(Model model) throws ZipException, IOException{
        System.out.println(LoaderUtil.getAllKarts().size());
        
        ResourceUtil.exportCharacter(LoaderUtil.getAllKarts().get(41));
        model.addAttribute("karts", LoaderUtil.getAllKarts());
        
        return "test";
    }
}
