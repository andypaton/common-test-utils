package com.alien.utils.core;

import java.io.File;
import java.util.Calendar;


public class Constants {

	final static public int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    final static public int CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH);
    final static public int CURRENT_DAY = Calendar.getInstance().get(Calendar.DATE);
    		
    /** An alias for File.separator */
    final static public String DIR_SEPARATOR = File.separator;
    
    /** The current path of the project */ 
    final static public String CURRENT_DIR = determineCurrentPath();
		
    
	/**
     * Defaults to "./" if there's an exception of any sort.
     * @warning Exceptions are swallowed.
     * @return Constants.DIR_SEPARATOR
     */
    final private static String determineCurrentPath() {
        try {
            return (new File(".").getCanonicalPath()) + Constants.DIR_SEPARATOR; 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "." + Constants.DIR_SEPARATOR;
    }



}

