package com.srb2kart.server.servermanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return destFile;
    }

    public static void unzip(String file, String des) throws IOException {
        String fileZip = file;
        File destDir = new File(des);
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(
            new WinZipInputStream(
            new FileInputStream(file)));
        try{
            ZipEntry zipEntry = zis.getNextEntry();
            while (zis.getNextEntry() != null) {
                
                while (zipEntry != null) {
                        File newFile = newFile(destDir, zipEntry);
                        if (zipEntry.isDirectory()) {
                            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                                throw new IOException("Failed to create directory " + newFile);
                            }
                        } else {
                            // fix for Windows-created archives
                            File parent = newFile.getParentFile();
                            if (!parent.isDirectory() && !parent.mkdirs()) {
                                throw new IOException("Failed to create directory " + parent);
                            }
                            
                            // write file content
                            FileOutputStream fos = new FileOutputStream(newFile);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }
                    zipEntry = zis.getNextEntry();;
                }
            }
                
            
        }catch(Exception ex){
            //TODO: Exception handling
        }finally{
            zis.closeEntry();
            zis.close();
        }
        
    }
    
}
