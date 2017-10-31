package com.alien.utils.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


/**
 * @description: Listens for a connection to be made to port, waiting a maximum timeout seconds
 * 
 * @example: 
 *     SocketHelper socketHelper = new SocketHelper();
 *     String socketStatus=socketHelper.portConnectionReceived(port, timeout);
 *     assertTrue(socketStatus.contains("Connection received"));
 */
public class SocketHelper {
	
    private static final Logger LOGGER = Logger.getLogger(SocketHelper.class.getName());

    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    

    public String portConnectionReceived(int port, int timeout) {

        try{
            providerSocket = new ServerSocket(port, 10);
            providerSocket.setSoTimeout(timeout*1000);
            
            LOGGER.debug("\nWaiting (max "+timeout+" seconds) for connection");
            
            connection = providerSocket.accept();

            return "Connection received from " + connection.getInetAddress().getHostName();
        }
        catch(java.io.InterruptedIOException e ) {
        	  return "Timed Out ("+timeout+" sec)!";
        }
        catch(IOException ioException){
            ioException.printStackTrace();
            return null;
        }
        finally{
            try{
                providerSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

}