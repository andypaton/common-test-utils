package com.alien.utils.webdriver.pageObjects;

import static com.alien.utils.webdriver.pageObjects.State.ELEMENT_IS_VISIBLE;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.alien.utils.webdriver.helpers.Constants;

public class PageObject {

    protected WebDriver webDriver;
	private int timeout = 60;

	public static final int ONE_SECOND = 1;
	public static final int FIVE_SECONDS = 5;
	public static final int THIRTY_SECONDS = 30;
	public static final int SIXTY_SECONDS = 60;
    
			
	public PageObject(WebDriver driver){
		this.webDriver = driver;
		initialize();
	}
	
	private void initialize() {
		PageFactory.initElements(webDriver, this);	        
	}
	
    public <T> void waitForPageToLoad(Class<T> clz) {
    	
        for (final Field field : clz.getDeclaredFields()) {
          	if (field.isAnnotationPresent(FindBy.class)) {
        		
                if (StringUtils.isNotBlank(field.getAnnotation(FindBy.class).id())) {
             
            	        waitForElement(By.id(field.getAnnotation(FindBy.class).id()), ELEMENT_IS_VISIBLE);
            
                }else if (StringUtils.isNotBlank(field.getAnnotation(FindBy.class).name())) {
                	
            	        waitForElement(By.name(field.getAnnotation(FindBy.class).name()), ELEMENT_IS_VISIBLE);
            
                }else if (StringUtils.isNotBlank(field.getAnnotation(FindBy.class).className())) {
             
            	        waitForElement(By.className(field.getAnnotation(FindBy.class).className()), ELEMENT_IS_VISIBLE);
            
                }else if (StringUtils.isNotBlank(field.getAnnotation(FindBy.class).xpath())) {
              
            	        waitForElement(By.xpath(field.getAnnotation(FindBy.class).xpath()), ELEMENT_IS_VISIBLE);
            
                }
        	}
        }
    }
    
    public void waitForLoad() {
    	
        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
                    }
                };
                
        WebDriverWait wait = new WebDriverWait(webDriver, THIRTY_SECONDS);
        wait.until(pageLoadCondition);
    }
    
	public <T> WebElement waitForElement(final By locator, State condition) {		
		
		new FluentWait<WebDriver>(webDriver)
				.withTimeout(SIXTY_SECONDS, TimeUnit.SECONDS)
				.pollingEvery(FIVE_SECONDS, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class)
				.until(expectedCondition(condition, locator));
		 
		return webDriver.findElement(locator);
	}
	
	public PageObject pause() {
		return pause(ONE_SECOND);
	}
	
	public PageObject pause(int timeInSeconds) {
		try {TimeUnit.SECONDS.sleep(timeInSeconds);} 
		catch (InterruptedException e) {e.printStackTrace();}
		return this;
	}
    
	private ExpectedCondition<WebElement> expectedCondition(State condition, By locator) {
		
		switch (condition) {
		case ELEMENT_IS_VISIBLE:
			return ExpectedConditions.visibilityOfElementLocated(locator);
		case ELEMENT_IS_CLICKABLE:
			return ExpectedConditions.elementToBeClickable(locator);
		default:
			throw new IllegalArgumentException("invalid condition");
		}
	}
	
	public boolean isElementPresent(By by) {  
		
	    webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);  
	    try {  
	            
	        	webDriver.findElement(by);  
	        return true;  
	    }  
	    catch(NoSuchElementException e) {
	        	
	        return false;  
	    }  
	    finally {
	        	
	        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  
	    }  
	}
	
	public void clickUntilElementIsDisplayed(WebElement webElementToClick, WebElement webElementToWaitFor){
		
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= Integer.valueOf(timeout * 1000)) {
        	try{
        		webElementToClick.click();
		        if (webElementToWaitFor.isDisplayed()){
		            break;
		        }
        	}
        	catch(Exception e){
        		pause(ONE_SECOND);
        	}
        }
	}
	
	/**
	 * This waits for a specified element on the page to be found on the page by the driver
	 * Uses the default test time out set by WebDriverSetup
	 * 
	 * @param	class	the class calling this method - used so can initialize the page class repeatedly
	 * @param 	driver	The webDriver
	 * @param	obj		The element you are waiting to display on the page
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isElementLoaded(WebElement obj){
		int count = 0;
		
		//set the timeout for looking for an element to 1 second as we are doing a loop and then refreshing the elements
		webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

		try{
			
	        long start = System.currentTimeMillis();
	        while (System.currentTimeMillis() - start <= Integer.valueOf(timeout * 1000)) {
	        	
	        	initialize();
	        	
	        	if (obj != null){
	        		break;
	        	}
	        }
				
		}catch( NullPointerException | NoSuchElementException |StaleElementReferenceException e){
			// do nothing
		}
		
		//set the timeout for looking for an element back to the default timeout
		webDriver.manage().timeouts().implicitlyWait(Constants.ELEMENT_TIMEOUT, TimeUnit.SECONDS);
		
		if (count < this.timeout){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * Overloaded method where you can specify the timeout 
	 * This waits for a specified element on the page to be found on the page by the driver
	 * 
	 * 
	 * @param	class	the class calling this method - used so can initialize the page class repeatedly
	 * @param 	driver	The webDriver
	 * @param	obj		The element you are waiting to display on the page
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isElementLoaded(WebElement obj, int timeout){
		this.timeout = timeout;
		return isElementLoaded(obj);
	}
	
	/**
	 * This uses the HTML DOM readyState property to wait until a page is finished loading.  It will wait for
	 * the ready state to be either 'interactive' or 'complete'.  
	 * 
	 * @param	class	the class calling this method - used so can initialize the page class repeatedly
	 * @param 	driver	The webDriver
	 * @param	obj		The element you are waiting to display on the page
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isDomInteractive(){

		Object obj = null;
		
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= Integer.valueOf(timeout * 1000)) {
        	
			obj = ((JavascriptExecutor) webDriver).executeScript("var result = document.readyState; return (result == 'complete' || result == 'interactive');");

			if (obj.equals(true)){
				return true;
			}
        }

		return false;
	}
	
	/**
	 * Overloaded method - gives option of specifying a timeout.
	 * This uses the HTML DOM readyState property to wait until a page is finished loading.  It will wait for
	 * the ready state to be either 'interactive' or 'complete'.  
	 * 
	 * @param 	driver	The webDriver
	 * @param	timeout	Integer value of number seconds to wait for a page to finish loaded before quiting
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isDomInteractive(int timeout){
		this.timeout = timeout;
		return isDomInteractive();
	}
	
	/**
	 * This uses protractor method to wait until a page is ready - notifyWhenNoOutstandingRequests
	 * 
	 * @param 	driver	The webDriver
	 */
	public static void waitForAngularRequestsToFinish(JavascriptExecutor driver) {
		
		driver.executeAsyncScript("var callback = arguments[arguments.length - 1];" +
    				"angular.element(document.body).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);");

    }
	
	/**
	 * A more strict version of isDomInteractive.  
	 * This uses the HTML DOM readyState property to wait until a page is finished loading.  It will wait for
	 * the ready state to be 'complete'.  
	 * 
	 * @param	class	the class calling this method - used so can initialize the page class repeatedly
	 * @param 	driver	The webDriver
	 * @param	obj		The element you are waiting to display on the page
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isDomComplete(){
		Object obj = null;
		
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= Integer.valueOf(timeout * 1000)) {
        	
			obj = ((JavascriptExecutor) webDriver).executeScript("var result = document.readyState; return (result == 'complete');");

			if (obj.equals(true)){
				return true;
			}
        }

		return false;
	}
	
	/**
	 * Overloaded method - gives option of specifying a timeout.
	 * A more strict version of isDomInteractive
	 * This uses the HTML DOM readyState property to wait until a page is finished loading.  It will wait for
	 * the ready state to be 'complete'.  
	 * 
	 * @param 	driver	The webDriver
	 * @param	timeout	Integer value of number seconds to wait for a page to finish loaded before quiting
	 * @return 	False if the element is not found after the timeout, true if is found
	 */
	public boolean isDomComplete(int timeout){
		this.timeout = timeout;
		return isDomInteractive();
	}
}
