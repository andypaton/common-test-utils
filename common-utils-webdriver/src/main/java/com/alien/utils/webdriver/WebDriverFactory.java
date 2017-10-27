package com.alien.utils.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;


public final class WebDriverFactory {
	
    private static final Logger LOGGER = Logger.getLogger(WebDriverFactory.class.getName());

    private static final int WAIT = 30;

    private static WebDriver WEB_DRIVER = null;
    
    private static final String CHROME_DRIVER_PATH = "./drivers/chrome_driver/chromedriver";
    private static final String GECKO_DRIVER_PATH = "./drivers/geckodriver";
    private static final String PHANTOMJS_DRIVER_PATH = "./drivers/phantom_driver/2_1_1/phantomjs";

    private static final String FIREFOX = "firefox";
    private static final String CHROME = "chrome";
    private static final String PHANTOMJS = "phantom";
    
    private static String webDriver;


    public static WebDriver getWebDriver() throws IOException{
    	
        if (System.getProperty("web.driver") != null) {
            webDriver = System.getProperty("web.driver");
            
            LOGGER.debug("WebDriver set to : " + webDriver);
        }
                
        if (WEB_DRIVER == null) {
        	
        		switch (webDriver){
        	
        		case CHROME: WEB_DRIVER = getChromeDriver(); break;
        		case FIREFOX: WEB_DRIVER = getFirefoxDriver(); break;
        		case PHANTOMJS: WEB_DRIVER = getPhantomJSDriver(); break;
        	
        		default: WEB_DRIVER = getFirefoxDriver();
        	        	
        		}

        		WEB_DRIVER.manage().window().maximize();

//        	WEB_DRIVER.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        	WEB_DRIVER.manage().timeouts().pageLoadTimeout(WAIT, TimeUnit.SECONDS);
        	
        		WEB_DRIVER.manage().timeouts().setScriptTimeout(WAIT, TimeUnit.SECONDS);
        }

        return WEB_DRIVER;
    }

    public static void clear() {
        WEB_DRIVER.quit();
        WEB_DRIVER = null;
    }
    
    public static boolean webDriverExists() {
        return (WEB_DRIVER != null);
    }
    
    private static String getAbsolutePath(String file){

    		ClassLoader classLoader = WebDriver.class.getClassLoader();
    		String path = new File(classLoader.getResource(file).getFile()).getAbsolutePath();
    	
    		LOGGER.debug("Path to WebDriver : " + path);
    	
    		return path;
    }
    
    private static WebDriver getChromeDriver(){

        final File chromeDriverFile = new File(getAbsolutePath(CHROME_DRIVER_PATH));
        if (chromeDriverFile.exists() && !chromeDriverFile.canExecute()) {
            chromeDriverFile.setExecutable(true);
        }
    	        
        System.setProperty("webdriver.chrome.driver", getAbsolutePath(CHROME_DRIVER_PATH));
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return new ChromeDriver(capabilities);
    }
    
    private static WebDriver getFirefoxDriver(){
    	
        System.setProperty("webdriver.gecko.driver", getAbsolutePath(GECKO_DRIVER_PATH));

    	    FirefoxProfile profile = new FirefoxProfile();
    	    profile.setAlwaysLoadNoFocusLib(true);
    	    return new FirefoxDriver(profile);
    }
    
    private static WebDriver getPhantomJSDriver() throws IOException{
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

        ArrayList<String> cliArgsCap = createCliArgs();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", true);
        capabilities.setCapability("takesScreenshot", true);
        capabilities.setJavascriptEnabled(true);
     
        final File phantomjsDriverFile = new File(getAbsolutePath(PHANTOMJS_DRIVER_PATH));
        if (phantomjsDriverFile.exists() && !phantomjsDriverFile.canExecute()) {
    	        phantomjsDriverFile.setExecutable(true);
        }

        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, getAbsolutePath(PHANTOMJS_DRIVER_PATH));

        return new PhantomJSDriver(capabilities);
    }

    private static ArrayList<String> createCliArgs() {
        ArrayList<String> cliArgsCap = new ArrayList<>();
        cliArgsCap.add("--ignore-ssl-errors=true");
        cliArgsCap.add("--web-security=false");
        cliArgsCap.add("--disk-cache=true");
        cliArgsCap.add("--max-disk-cache-size=256");
        cliArgsCap.add("--proxy-type=none");
        return cliArgsCap;
    }

}
