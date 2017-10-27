package com.alien.utils.webdriver.by.angular;
/*
Copyright 2007-2011 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */


import java.io.Serializable;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * Mechanism used to locate elements within a document. In order to create your own locating
 * mechanisms, it is possible to subclass this class and override the protected methods as required,
 * though it is expected that that all subclasses rely on the basic finding mechanisms provided
 * through static methods of this class:
 * 
 * <code>
 * public WebElement findElement(WebDriver driver) {
 *     WebElement element = driver.findElement(By.id(getSelector()));
 *     if (element == null)
 *       element = driver.findElement(By.name(getSelector());
 *     return element;
 * }
 * </code>
 */
public abstract class ByNG {

  /**
   * @param ngRepeater The value of the "ngButtonText" attribute to search for
   * @return a By which locates elements by the value of the "ngButtonText" attribute.
   */
  public static ByNG ngButtonText(final String ngButtonText) {
    if (ngButtonText == null)
      throw new IllegalArgumentException(
          "Cannot find elements when ngRepeater text is null.");

    return new ByNGButton(ngButtonText);
  }
  
  /**
   * @param ngController The value of the "ngController" attribute to search for
   * @return a By which locates elements by the value of the "ngController" attribute.
   */
  public static ByNG ngController(final String ngController) {
  /* @param ngRepeater The value of the "ngRepeater" attribute to search for
   * @return a By which locates elements by the value of the "ngRepeater" attribute.
   */
    if (ngController == null)
      throw new IllegalArgumentException(
          "Cannot find elements when ngRepeater text is null.");

    return new ByNGController(ngController);
  } 
  
  /**
   * @param ngModel The value of the "ngModel" attribute to search for
   * @return a By which locates elements by the value of the "ngModel" attribute.
   */
  public static ByNG ngModel(final String ngModel) {
  /* @param ngRepeater The value of the "ngRepeater" attribute to search for
   * @return a By which locates elements by the value of the "ngRepeater" attribute.
   */
    if (ngModel == null)
      throw new IllegalArgumentException(
          "Cannot find elements when ngRepeater text is null.");

    return new ByNGModel(ngModel);
  }
  
  /**
   * @param ngRepeater The value of the "ngRepeater" attribute to search for
   * @return a By which locates elements by the value of the "ngRepeater" attribute.
   */
 public static ByNG ngRepeater(final String ngRepeater) {
    if (ngRepeater == null)
      throw new IllegalArgumentException(
          "Cannot find elements when ngRepeater text is null.");

    return new ByNGRepeater(ngRepeater);
  }

  /**
   * Find a single element. Override this method if necessary.
   * 
   * @param context A context to use to find the element
   * @return The WebElement that matches the selector
   */
  public WebElement findElement(SearchContext context) {
    List<WebElement> allElements = findElements(context);
    if (allElements == null || allElements.isEmpty())
      throw new NoSuchElementException("Cannot locate an element using "
          + toString());
    return allElements.get(0);
  }

  /**
   * Find many elements.
   * 
   * @param context A context to use to find the element
   * @return A list of WebElements matching the selector
   */
  public abstract List<WebElement> findElements(SearchContext context);

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ByNG byNG = (ByNG) o;

    return toString().equals(byNG.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    // A stub to prevent endless recursion in hashCode()
    return "[unknown locator]";
  }

  public static class ByNGButton extends ByNG implements Serializable {

	    private static final long serialVersionUID = 376317282960469555L;

	    private final String name;

	    public ByNGButton(String name) {
	      this.name = name;
	    }

	    @Override
	    public List<WebElement> findElements(SearchContext context) {
	      if (context instanceof FindsByNGButtonText)
	        return ((FindsByNGButtonText) context).findElementsByNGButton(name);
	      return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
	          + name + "']");
	    }

	    @Override
	    public WebElement findElement(SearchContext context) {
	      if (context instanceof FindsByNGButtonText)
	        return ((FindsByNGButtonText) context).findElementByNGButton(name);
	      return (WebElement) ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
	          + name + "']");
	    }

	    @Override
	    public String toString() {
	      return "By.name: " + name;
	    }
	  }
  
  public static class ByNGModel extends ByNG implements Serializable {

    private static final long serialVersionUID = 376317282960469555L;

    private final String name;

    public ByNGModel(String name) {
      this.name = name;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      if (context instanceof FindsByNGModel)
        return ((FindsByNGModel) context).findElementsByNGModel(name);
      return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public WebElement findElement(SearchContext context) {
      if (context instanceof FindsByNGModel)
        return ((FindsByNGModel) context).findElementByNGModel(name);
      return (WebElement) ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public String toString() {
      return "By.name: " + name;
    }
  }
  
  public static class ByNGController extends ByNG implements Serializable {

	    private static final long serialVersionUID = 376317282960469555L;

	    private final String name;

	    public ByNGController(String name) {
	      this.name = name;
	    }

	    @Override
	    public List<WebElement> findElements(SearchContext context) {
	      if (context instanceof FindsByNGContoller)
	        return ((FindsByNGContoller) context).findElementsByNGContoller(name);
	      return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
	          + name + "']");
	    }

	    @Override
	    public WebElement findElement(SearchContext context) {
	      if (context instanceof FindsByNGContoller)
	        return ((FindsByNGContoller) context).findElementByNGContoller(name);
	      return (WebElement) ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
	          + name + "']");
	    }

	    @Override
	    public String toString() {
	      return "By.name: " + name;
	    }
	  }
  
  public static class ByNGRepeater extends ByNG implements Serializable {
    private static final long serialVersionUID = 376317282960469555L;

    private final String name;

    public ByNGRepeater(String name) {
      this.name = name;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      if (context instanceof FindsByNGRepeat)
        return ((FindsByNGRepeat) context).findElementsByNGRepeat(name);
      return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public WebElement findElement(SearchContext context) {
      if (context instanceof FindsByNGRepeat)
        return ((FindsByNGRepeat) context).findElementByNGRepeat(name);
      return (WebElement) ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public String toString() {
      return "By.name: " + name;
    }
  }

}