package com.srb2kart.services;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LogServices extends Thread {

    private boolean doStop = false;

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    public void run(){
        System.out.println("MyThread running");
        String fileName = "res/server/log.txt";
            try {
                RandomAccessFile bufferedReader = new RandomAccessFile( fileName, "r" 
                );
    
                long filePointer;
                while ( keepRunning() ) {
                    final String string = bufferedReader.readLine();
    
                    if ( string != null )
                        System.out.println( string );
                    else {
                        filePointer = bufferedReader.getFilePointer();
                        bufferedReader.close();
                        Thread.sleep( 2500 );
                        bufferedReader = new RandomAccessFile( fileName, "r" );
                        bufferedReader.seek( filePointer );
                    }
                }

            } catch ( IOException | InterruptedException e ) {
                e.printStackTrace();
            }
     }
}
