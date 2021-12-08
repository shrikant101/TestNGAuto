package pages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.internal.Locatable;
import com.relevantcodes.extentreports.LogStatus;

import utils.ExtentReports.ExtentTestManager;

public class BasePage {
	public WebDriver driver;
	public WebDriverWait wait;
	public static long implicitWait = 10L;
	public static long sleepInMillis = 3000L;
	private static final Logger LOGGER = Logger.getLogger(BasePage.class);

	// Constructor
	public BasePage(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
	}

	// Write Text
	public void writeText(By elementLocation, String text) {
		driver.findElement(elementLocation).sendKeys(text);
	}

	// Read Text
	public String readText(By elementLocation) {
		return driver.findElement(elementLocation).getText();
	}

	/**
	 * This method will click on the element.
	 * 
	 * @author shripras
	 * @param logger    Boolean value to specify if logging and snapshot needed in
	 *                  the report
	 * @param multiArgs Additional fields for locator and data
	 * @return none
	 */
	public void click(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		WebElement targetElement = null;

		int varargs = args.length;
		switch (varargs) {

		case 1:
			targetElement = findElementInner(uiGenericLocator);
			targetElement.click();
			waitForPageToSynchronizedWithServer(driver);
			//v waitForPageToFinishLoading(driver);

			uiGenericLocator = formatForLogging(uiGenericLocator);

			if (logging) {
				ExtentTestManager.getTest().log(LogStatus.PASS, "Clicked successfully on  '" + uiGenericLocator + "'",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				LOGGER.info("Clicked successfully on  " + uiGenericLocator);
			} else {
				LOGGER.info("Clicked successfully on  " + uiGenericLocator);
			}

			break;
		case 2:
			if (uiGenericLocator.contains("__LABEL__")) {
				uiElement = args[1];
				uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			}
			if (uiGenericLocator.contains("__DATA__")) {
				uiElement = args[1];
				uiGenericLocator = getLocatorWithData(uiGenericLocator, uiElement);
			}

			targetElement = findElementInner(uiGenericLocator);
			targetElement.click();
			waitForPageToSynchronizedWithServer(driver);
			//v waitForPageToFinishLoading(driver);

			if (logging) {
				ExtentTestManager.getTest().log(LogStatus.PASS, "Clicked field '" + uiElement + "' successfully",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			} else {
				ExtentTestManager.getTest().log(LogStatus.PASS, "Clicked field '" + uiElement + "' successfully",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			}

			break;

		case 3:
			uiElement = args[1];
			uiData = args[2];
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
			targetElement = findElementInner(uiGenericLocator);
			targetElement.click();

			if (logging) {
				ExtentTestManager.getTest().log(LogStatus.PASS,
						"Clicked field '" + uiElement + "' with data '" + uiData + "' successfully",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			} else {
				ExtentTestManager.getTest().log(LogStatus.PASS,
						"Clicked field '" + uiElement + "' with data '" + uiData + "' successfully",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			}

			break;
		default:
			break;
		}

	}

	/**
	 * This method will tab on the element.
	 * 
	 * @author shripras
	 * @param string locator where tab needs to be pressed
	 * @return none
	 */
	public void tab(String strdocumentType) {
		WebElement documentType = null;
		try {
			documentType = driver.findElement(By.xpath(strdocumentType));
			isSynchronizedWithServer();
			if (documentType.isDisplayed()) {
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", documentType);
				} catch (Exception e) {
					LOGGER.warn("Exception in performing the scrollIntoView operation of findElementBy(): "
							+ e.getMessage());
				}
			}
			documentType.sendKeys(Keys.TAB);
			Thread.sleep(2000L);
		} catch (Exception e) {
			LOGGER.warn("Exception in Syncwithserver: " + e.getMessage());
		}
		waitForPageToSynchronizedWithServer(driver);
		//v waitForPageToFinishLoading(driver);
	}

	public boolean isSynchronizedWithServer() {
		boolean flag = false;
		int waiter = 0;

		try {
			ClientSynchedWithServer clientSynchedWithServer = new ClientSynchedWithServer();
			Boolean jsReturned = clientSynchedWithServer.apply(driver);
			while (!jsReturned) {
				waiter++;
				Thread.sleep(1000);

				if (waiter > 10) {
					LOGGER.warn("Syncwithserver wait is timed out");
					break;
				}
				jsReturned = clientSynchedWithServer.apply(driver);
			}

			if (clientSynchedWithServer.apply(driver)) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
			LOGGER.warn("Exception in Syncwithserver: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method will wait for AdfPage to finish sync with server.
	 * 
	 * @author JADF reused
	 * @param webDriver        The WebDriver instance to pass to the expected
	 *                         conditions
	 * @param timeOutInSeconds The timeout in seconds when an expectation is called
	 * @param sleepInMillis    The duration in milliseconds to sleep between polls.
	 * @return true, if successful
	 */
	public static boolean waitForPageToSynchronizedWithServer(WebDriver webDriver) {
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				JavascriptExecutor jsexecutor = (JavascriptExecutor) driver;
				Boolean isReady = (Boolean) jsexecutor
						.executeScript("return window.AdfPage.PAGE.isSynchronizedWithServer()");
				return isReady;
			}
		};

		WebDriverWait webDriverWait = new WebDriverWait(webDriver, implicitWait, sleepInMillis);

		return webDriverWait.until(expectedCondition);
	}

	/**
	 * This method will wait for AdfPage to finish loading.
	 * 
	 * @author shripras JADF reused
	 * @param webDriver        The WebDriver instance to pass to the expected
	 *                         conditions
	 * @param timeOutInSeconds The timeout in seconds when an expectation is called
	 * @param sleepInMillis    The duration in milliseconds to sleep between polls.
	 * @return true, if successful
	 */
	public static boolean waitForPageToFinishLoading(WebDriver webDriver) {
		// try {
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				JavascriptExecutor jsexecutor = (JavascriptExecutor) driver;
				Boolean isReady = (Boolean) jsexecutor.executeScript("return window.AdfPage.PAGE.isPageFullyLoaded()");
				return isReady;
			}
		};
		WebDriverWait webDriverWait = new WebDriverWait(webDriver, implicitWait, sleepInMillis);
		// webDriverWait.ignoring(WebDriverException.class);
		return webDriverWait.until(expectedCondition);
	}

	/**
	 * This method will click on the element.
	 * 
	 * @author shripras
	 * @internal args
	 * @param arg Field to clear
	 * @return none
	 */
	private void clear(String lblDocumentType2) {
		try {
			driver.findElement(By.xpath(lblDocumentType2)).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * 
	 * @param uiGenericLocator locator with 1fields as __LABEL__
	 * @param uiElement        value to be replaced with __LABEL__
	 * @param conditionalValue provides the condition on the basis of which check or
	 *                         uncheck can be determined
	 */

	private void revertDelay() {
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);

	}

	private void nodelay() {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

	}

	/**
	 * 
	 * @author shripras
	 * @param uiGenericLocator generic locator with 2 runtime value to be replaced
	 *                         ie __LABEL__ and __DATA__
	 * @param uiElement        replaced the UI element needed on the UI ie replaces
	 *                         __LABEL__ with 2nd arguement
	 * @param testdata         provides the condition on the basis of which check or
	 *                         uncheck can be determined ie replaces __DATA__ with
	 *                         3rd argument
	 */
	public void verifyCheckBox(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		int varargs = args.length;
		if (!(args[varargs - 1].equalsIgnoreCase("") || args[varargs - 1].equalsIgnoreCase("N/A")
				|| args[varargs - 1].equalsIgnoreCase("NA"))) {
			switch (varargs) {
			case 2:
				testdata = args[1];
				if (testdata.equalsIgnoreCase("YES") || testdata.equalsIgnoreCase("Checked")
						|| testdata.equalsIgnoreCase("NO") || testdata.equalsIgnoreCase("Unchecked")) {
					if (testdata.equalsIgnoreCase("YES") || testdata.equalsIgnoreCase("Checked")) {
						uiGenericLocator = getLocatorWithData(uiGenericLocator, "Checked");
					} else if (testdata.equalsIgnoreCase("NO") || testdata.equalsIgnoreCase("Unchecked")) {
						uiGenericLocator = getLocatorWithData(uiGenericLocator, "Unchecked");
					}
					try {
						assertTrue(findElementInner(uiGenericLocator).isDisplayed());

						if (logging) {
							ExtentTestManager.getTest().log(LogStatus.PASS, "Verified checkbox successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						} else {
							ExtentTestManager.getTest().log(LogStatus.PASS, "Verified field successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						}
					} catch (Exception e) {
						LOGGER.fatal("Element not found with value " + uiGenericLocator);
						ExtentTestManager.getTest().log(LogStatus.FAIL,
								"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				} else {
					LOGGER.fatal("Invalid data provided" + testdata);
					ExtentTestManager.getTest()
							.log(LogStatus.FAIL, "Error occured while verifying checkbox value " + uiGenericLocator
									+ " for value " + testdata,
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}
				break;
			case 3:
				uiElement = args[1];
				testdata = args[2];
				if (testdata.equalsIgnoreCase("YES") || testdata.equalsIgnoreCase("Checked")
						|| testdata.equalsIgnoreCase("NO") || testdata.equalsIgnoreCase("Unchecked")) {
					if (testdata.equalsIgnoreCase("YES") || testdata.equalsIgnoreCase("Checked")) {
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
						uiGenericLocator = getLocatorWithData(uiGenericLocator, "Checked");
					} else if (testdata.equalsIgnoreCase("NO") || testdata.equalsIgnoreCase("Unchecked")) {
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
						uiGenericLocator = getLocatorWithData(uiGenericLocator, "Unchecked");
					}
					try {
						assertTrue(findElementInner(uiGenericLocator).isDisplayed());

						if (logging) {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field  '" + uiElement + "' with data '" + testdata + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						} else {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field  '" + uiElement + "' with data '" + testdata + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						}
					} catch (Exception e) {
						LOGGER.fatal("Element not found with value " + uiGenericLocator);
						ExtentTestManager.getTest().log(LogStatus.FAIL,
								"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				} else {
					LOGGER.fatal("Invalid data provided " + testdata);
					ExtentTestManager.getTest().log(LogStatus.FAIL,
							"Error occured while verifying checkbox value " + uiElement + " for value " + testdata,
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}
				break;
			default:
				break;
			}
		}

	}

	private String getSnapshot() {
		return "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
	}

	/**
	 * This method will check the presence of any element on the UI
	 * 
	 * @author shripras
	 * @param logger    Boolean value to specify if logging and snapshot needed in
	 *                  the report
	 * @param multiArgs Additional fields for locator and data
	 * @return none
	 */
	public void verifyElementL2(String uiGenericLocator, String uiElement, String xlValues) {

		if (xlValues.equalsIgnoreCase("YES")) {
			String uiLocatorAttribute = getLocatorWithData(uiGenericLocator, "Checked");
			uiLocatorAttribute = getLocatorWithLabel(uiLocatorAttribute, uiElement);
			assertTrue(driver.findElement(By.xpath(uiLocatorAttribute)).isDisplayed());
		} else if (xlValues.equalsIgnoreCase("NO")) {
			String uiLocatorAttribute = getLocatorWithData(uiGenericLocator, "Unchecked");
			uiLocatorAttribute = getLocatorWithLabel(uiLocatorAttribute, uiElement);
			assertTrue(driver.findElement(By.xpath(uiLocatorAttribute)).isDisplayed());
		} else {
			LOGGER.fatal("Invalid data provided" + xlValues);
		}

	}

	/***
	 * Replace __DATA__ string present in 1st arguement with 2nd arguement Asserts
	 * if element is present on the UI
	 * 
	 * @param uiGenericLocator
	 * @param testdata
	 */

	public void verifyElementPresent(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		int varargs = args.length;
	
		if (!(args[varargs - 1].equalsIgnoreCase("") || args[varargs - 1].equalsIgnoreCase("N/A") || args[varargs - 1].equalsIgnoreCase("NA"))) {
			switch (varargs) {
			case 1:// snap,xpath

				try {
					assertTrue(findElementInner(uiGenericLocator).isDisplayed());

					/* if (logging) { */
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified element presence successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					/*
					 * } else { ExtentTestManager.getTest().log(LogStatus.PASS,
					 * "Verified element presence successfully"); }
					 */
				} catch (Exception e) {
					LOGGER.fatal("Element not found : " + uiGenericLocator);
					ExtentTestManager.getTest().log(LogStatus.FAIL, "Field : '" + uiGenericLocator + "' is not present",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}

				break;
			case 2:// snap,xpath,data or //snap,xpath,lbl
				if (uiGenericLocator.contains("__LABEL__") || (uiGenericLocator.contains("__DATA__"))) {
					if (uiGenericLocator.contains("__LABEL__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
					}
					if (uiGenericLocator.contains("__DATA__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithData(uiGenericLocator, uiElement);
					}
					try {
						assertTrue(findElementInner(uiGenericLocator).isDisplayed());

						if (logging) {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field '" + uiElement + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						} else {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field '" + uiElement + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						}
					} catch (Exception e) {
						LOGGER.fatal("Element not found with value " + uiElement);
						ExtentTestManager.getTest().log(LogStatus.FAIL,
								"Field : '" + uiGenericLocator + "' \n does not not have value '" + uiElement + "'",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				}

				break;
			case 3:// snap,xpath,lbl,data or //snap,xpath,lbl,data
				if (uiGenericLocator.contains("__LABEL__") || (uiGenericLocator.contains("__DATA__"))) {
					if (uiGenericLocator.contains("__LABEL__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
					}
					if (uiGenericLocator.contains("__DATA__")) {
						uiData = args[2];
						uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
					}
					try {
						// assertTrue(findElementInner(uiGenericLocator).isDisplayed());
						assertTrue(findElementInner(uiGenericLocator).isDisplayed());
						if (logging) {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field  '" + uiElement + "' with data '" + uiData + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						} else {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Verified field  '" + uiElement + "' with data '" + uiData + "' successfully",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						}
					} catch (Exception e) {
						LOGGER.fatal("Element not found with value " + uiData);
						ExtentTestManager.getTest().log(LogStatus.FAIL,
								"Field : '" + uiElement + "' \n does not not have value '" + uiData + "'",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				}

				break;
			case 4:// snap,xpath,lbl,data or //snap,xpath,lbl,data
				testdata = args[3];
				if (uiGenericLocator.contains("__LABEL__") || (uiGenericLocator.contains("__DATA__"))) {
					if (uiGenericLocator.contains("__LABEL__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
					}
					if (uiGenericLocator.contains("__DATA__")) {
						uiData = args[2];
						uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
					}
				}
				verifyElementWithData(logging, uiGenericLocator, testdata);
				break;

			default:
				break;
			}
		}
	}

	public void verifyElementWithData(Boolean logging, String ...args) {
		if (!(args[args.length - 1].equalsIgnoreCase("") || args[args.length - 1].equalsIgnoreCase("N/A")
				|| args[args.length - 1].equalsIgnoreCase("NA"))) {
		Pattern pattern = Pattern.compile("\\|");
		String uiGenericLocator = args[0];
		String testdata = args[args.length -1];
		testdata = cleanupData(testdata);
		String uiRuntimeLocator = null;
		if (testdata.contains("|")) {
			String[] allVal = pattern.split(testdata);
			for (String singleVal : allVal) {
				try {
					
					if (uiGenericLocator.contains("__LABEL__")) {
						uiRuntimeLocator = getLocatorWithLabel(uiGenericLocator, singleVal);
					}
					else if (uiGenericLocator.contains("__DATA__")) {
						uiRuntimeLocator = getLocatorWithData(uiGenericLocator, singleVal);
					}

					assertTrue(findElementInner(uiRuntimeLocator).isDisplayed());

					if (logging) {
						ExtentTestManager.getTest().log(LogStatus.PASS,
								"Verified field '" + singleVal + "' successfully",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					} else {
						ExtentTestManager.getTest().log(LogStatus.PASS,
								"Verified field '" + singleVal + "' successfully",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				} catch (Exception e) {
					LOGGER.fatal("Element not found with value " + testdata);
					ExtentTestManager.getTest().log(LogStatus.FAIL,
							"Field : '" + uiGenericLocator + "' \n does not not have value '" + singleVal + "'",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}

			}
		} else {
			try {
				uiGenericLocator = getLocatorWithData(uiGenericLocator, testdata);
				assertTrue(findElementInner(uiGenericLocator).isDisplayed());

				if (logging) {
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified field '" + testdata + "' successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				} else {
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified field '" + testdata + "' successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}
			} catch (Exception e) {
				LOGGER.fatal("Element not found with value " + testdata);
				ExtentTestManager.getTest().log(LogStatus.FAIL,
						"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			}
		}
		}
	}
	
	
	
	public void verifyTableRow(Boolean logging, String ...args) {
		if (!(args[args.length - 1].equalsIgnoreCase("") || args[args.length - 1].equalsIgnoreCase("N/A")
				|| args[args.length - 1].equalsIgnoreCase("NA"))) {
		Pattern pattern = Pattern.compile("\\|");
		Pattern pattern2 = Pattern.compile("\\:");
		String uiGenericLocator = args[0];
		String testdata = args[args.length -1];
		testdata = cleanupData(testdata);
		String uiRuntimeLocator = null;
		String[] keyValue;
		if (testdata.contains("|")) {
			String[] allVal = pattern.split(testdata);
			for (String singleVal : allVal) {
				try {
						if (singleVal.contains(":")) {
							keyValue = pattern2.split(singleVal);
							uiRuntimeLocator = uiGenericLocator;
							//logic to split ':' separated values
							for (String rowVal : keyValue) {
									uiRuntimeLocator = getFirstLabelOccuranceReplaced(uiRuntimeLocator, rowVal);
							} 
							try {
							assertTrue(findElementInner(uiRuntimeLocator).isEnabled());
							uiRuntimeLocator = "";
							if (logging) {
								ExtentTestManager.getTest().log(LogStatus.PASS,
										"Verified field '" + singleVal + "' successfully",
										ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
							} else {
								ExtentTestManager.getTest().log(LogStatus.PASS,
										"Verified field '" + singleVal + "' successfully",
										ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
							}
							} catch (Exception e) {
								uiRuntimeLocator = formatForLogging(uiRuntimeLocator);
								LOGGER.fatal("Row Not found with value " + keyValue);
								ExtentTestManager.getTest().log(LogStatus.FAIL,
										"Field : '" + uiGenericLocator + "' \n does not not have value '" + singleVal + "'",
										ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
							}
						}
				} catch (Exception e) {
					LOGGER.fatal("Element not found with value " + singleVal);
					ExtentTestManager.getTest().log(LogStatus.FAIL,
							"Field : '" + uiGenericLocator + "' \n does not not have value '" + singleVal + "'",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}

			}
		} else {
			try {
				uiGenericLocator = getLocatorWithData(uiGenericLocator, testdata);
				assertTrue(findElementInner(uiGenericLocator).isDisplayed());

				if (logging) {
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified field '" + testdata + "' successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				} else {
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified field '" + testdata + "' successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}
			} catch (Exception e) {
				LOGGER.fatal("Element not found with value " + testdata);
				ExtentTestManager.getTest().log(LogStatus.FAIL,
						"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			}
		}
		}
	}

	public void verifyElementWithDataAndValue(Boolean logging, String ...args) {
		
		String uiGenericLocator = args[0];
		String uiGenericLocatorUpdated = null;
		String uiElement = args[1];
		String testdata = args[2];
		int varargs = args.length;
	
		
		if (!(args[varargs - 1].equalsIgnoreCase("") || args[varargs - 1].equalsIgnoreCase("N/A")
				|| args[varargs - 1].equalsIgnoreCase("NA"))) {
			Pattern pattern = Pattern.compile("\\|");
			testdata = cleanupData(testdata);
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			if (testdata.contains("|")) {
				
				//removing child elements to check total count of parent
				//uiGenericLocator = uiGenericLocator.replace("/td/span[.='__DATA__']","");
				//int expectedFromUI = driver.findElements(By.xpath(uiGenericLocator)).size();
				String[] allVal = pattern.split(testdata);
				//int totalVal = allVal.length;
				//below if condition to check if the total number of entries in the table matches with the number of entries given in xl input sheet.
				//if ( expectedFromUI == totalVal)
				//{
				
					for (String singleVal : allVal) {
						// make xpath from each value
						try {
							uiGenericLocatorUpdated = getLocatorWithData(uiGenericLocator, singleVal);

							assertTrue(findElementInner(uiGenericLocatorUpdated).isDisplayed());
							if (logging) {
								ExtentTestManager.getTest().log(LogStatus.PASS,
										"Verified field '" + uiElement + "' with value '" + singleVal + "' successfully",
										ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
							} else {
								ExtentTestManager.getTest().log(LogStatus.PASS,
										"Verified field '" + uiElement + "' with value '" + singleVal + "' successfully",
										ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
							}
						} catch (Exception e) {
							LOGGER.fatal("Element not found with value " + testdata);
							ExtentTestManager.getTest().log(LogStatus.FAIL,
									"Field : '" + uiGenericLocatorUpdated + "' \n does not not have value '" + singleVal + "'",
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						}

					}
				/*}
				else {
					LOGGER.fatal("Element count mismatch.Total element in table is  " + expectedFromUI + " but found "+totalVal);
					ExtentTestManager.getTest().log(LogStatus.FAIL,
							"Element count mismatch.Total element in table is  " + expectedFromUI + " but found "+totalVal,
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}*/
			} else {
				try {

					uiGenericLocator = getLocatorWithData(uiGenericLocator, testdata);
					assertTrue(findElementInner(uiGenericLocator).isDisplayed());

					if (logging) {
						ExtentTestManager.getTest().log(LogStatus.PASS,
								"Verified field '" + uiElement + "' with value '" + testdata + "' successfully",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					} else {
						ExtentTestManager.getTest().log(LogStatus.PASS,
								"Verified field '" + uiElement + "' with value '" + testdata + "' successfully",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				} catch (Exception e) {
					LOGGER.fatal("Element not found with value " + testdata);
					ExtentTestManager.getTest().log(LogStatus.FAIL,
							"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}
			}
		}

	}

	public String cleanupData(String xlValues) {

		if (xlValues.contains("'\n")) {
			xlValues = xlValues.replaceFirst("\n", "");
		}
		return xlValues;

	}

	/**
	 * @ param structure possible @ 2 args locator,data @ 3 args
	 * locator,data,snapshotflag @ 4 args
	 * locator,replaceable_LABEL,data,snapshotflag
	 */
	public void selectByLabel(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		int varargs = args.length;
		switch (varargs) {
		case 2:
			testdata = args[1];
			break;
		case 3:
			uiElement = args[1];
			testdata = args[2];
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			break;
		case 4:
			uiElement = args[1];
			uiData = args[2];
			testdata = args[3];
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
			break;
		default:
			testdata = args[1];
			break;
		}
		try {
			WebElement eledocumentType = findElementInner(uiGenericLocator);
			uiGenericLocator = formatForLogging(uiGenericLocator);

			if (isSelectBox(eledocumentType)) {
				Select targetSelector = new Select(eledocumentType);
				targetSelector.selectByVisibleText(testdata);

				if (logging) {
					ExtentTestManager.getTest().log(LogStatus.PASS, "Selected element with value " + testdata + "'",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					LOGGER.info("Selected element with value " + testdata);
				} else {
					LOGGER.info("Selected element with value " + testdata);
				}
			}
			else {
				LOGGER.fatal("Unable to select element with value " + testdata);
				ExtentTestManager.getTest().log(LogStatus.FAIL,
						"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
	
			}
		} catch (Exception e) {
			LOGGER.fatal("Unable to select element with value " + testdata);
			ExtentTestManager.getTest().log(LogStatus.FAIL,
					"Field : '" + uiGenericLocator + "' \n does not not have value " + testdata,
					ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));

			
		}

		waitForPageToSynchronizedWithServer(driver);
		//v waitForPageToFinishLoading(driver);
	}

	/**
	 * isSelectBox is an internal /private method that verifies given element is
	 * select box or not.
	 * 
	 * @param element the element
	 * @return : returns true if operation is successfully,and false if operation
	 *         fails
	 */
	public boolean isSelectBox(WebElement element) {
		boolean isSelectbox = false;

		try {
			if (element.getTagName().equalsIgnoreCase("SELECT")) {
				isSelectbox = true;
			}
		} catch (Exception e) {
			isSelectbox = false;
			LOGGER.error("Checking the element is a valid select element results in an error:: " + e.getMessage());
		}
		return isSelectbox;
	}

	/**
	 * @ param structure possible @ 2 args locator,data @ 3 args
	 * locator,data,snapshotflag @ 4 args
	 * locator,replaceable_LABEL,data,snapshotflag
	 */
	public void enter(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		int varargs = args.length;
		switch (varargs) {
		case 2:
			testdata = args[1];
			break;
		case 3:
			uiElement = args[1];
			testdata = args[2];
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			break;
		case 4:
			uiElement = args[1];
			uiData = args[2];
			testdata = args[3];
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
			break;
		default:
			testdata = args[1];
			break;
		}

		WebElement eledocumentType = findElementInner(uiGenericLocator);
		eledocumentType.clear();
		eledocumentType.sendKeys(testdata);
		uiGenericLocator = formatForLogging(uiGenericLocator);
		ExtentTestManager.getTest().log(LogStatus.PASS, "Enter value " + testdata,
				ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
		waitForPageToSynchronizedWithServer(driver);
		//v waitForPageToFinishLoading(driver);
	}

	private WebElement findElementInner(String locator) {
		WebElement locatorVal = null;
		try {
			locator = cleanupData(locator);
			isSynchronizedWithServer();
			locatorVal = driver.findElement(By.xpath(locator));
			if (locatorVal.isDisplayed()) {
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", locatorVal);
				} catch (Exception e) {
					LOGGER.warn("Exception in performing the scrollIntoView operation of findElementBy(): "
							+ e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Element not found : " + locator);
		}
		return locatorVal;
	}

	public String getLocatorWithData(String GenericXpath, String currentValue) {
		return GenericXpath.replace("__DATA__", currentValue);
	}

	public String getLocatorWithLabel(String GenericXpath, String currentValue) {

		return GenericXpath.replace("__LABEL__", currentValue);
	}
	
	public String getFirstLabelOccuranceReplaced(String GenericXpath, String currentValue) {

		return GenericXpath.replaceFirst("__LABEL__", currentValue);
	}

	public void clearScreen() {
		try {
			driver.navigate().refresh();
			waitForPageToSynchronizedWithServer(driver);
			//v waitForPageToFinishLoading(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readyAUT(Boolean logging, String... args) {
		try {
			// 1st args to check if element is present. This should be the entry criteria
			// for the page to be ready and then refresh page.
			// 2nd arg can be provided to do a specific action after refresh is done.
			if (args.length == 2) {
				String uiGenericLocator = args[0];
				driver.findElement(By.xpath(uiGenericLocator));
			}
			if (args.length == 3) {
				String uiGenericLocator = args[0];
				String uiElement = args[1];
				uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
				driver.findElement(By.xpath(uiGenericLocator));
				}
			
		} catch (Exception e) {
			clearScreen();
			String uiGenericLocator = args[args.length - 1];
			WebElement eledocumentType = findElementInner(uiGenericLocator);
			runJavascriptOnElement("arguments[0].click();", eledocumentType);

			if (logging) {
				ExtentTestManager.getTest().log(LogStatus.PASS, "Clicked successfully on  '" + uiGenericLocator + "'",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				LOGGER.info("Clicked successfully on  '" + uiGenericLocator + "'");
			} else {
				LOGGER.info("Clicked successfully on  '" + uiGenericLocator + "'");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void mouseoverandclick(String uiGenericLocator) {
		WebElement mainMenuElement = findElementInner(uiGenericLocator);
		WebElement submenuElement;

		try {
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath(uiGenericLocator));
			action.moveToElement(we).click().build().perform();
			formatForLogging(uiGenericLocator);
			ExtentTestManager.getTest().log(LogStatus.PASS, "Mousehover and clicked on  " + uiGenericLocator,
					ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception in finding either the mainmenu or submenu item" + e.getMessage());
			// fail
			ExtentTestManager.getTest().log(LogStatus.FAIL,
					"Unable to mousehover and click on  '" + uiGenericLocator + "'",
					ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
		}
	}

	/**
	 * Initialize log4j logger instance.
	 */
	public static void intiateLoggers() {
		try {
			System.setProperty("log4j.time", Long.toString(Calendar.getInstance().getTimeInMillis()));
			// String logfileSyntax = String.format("Test" + "_%s.log",
			// Calendar.getInstance().getTimeInMillis());
			// String logfileName = System.getProperty("user.dir")+"/"+logfileSyntax;
			System.setProperty("pwd", System.getProperty("user.dir"));
			String logfileName = "log4j.properties";
			PropertyConfigurator.configure(logfileName);
			LOGGER.info("log4j properties filepath: " + logfileName);
		} catch (Exception e) {
			LOGGER.error("Error while intiating Loggers.", e);
		}
	}

	public void clickusingjs(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		int varargs = args.length;
		switch (varargs) {
		case 1:
			// innerClick(snapshot, uiGenericLocator);
			WebElement eledocumentType = findElementInner(uiGenericLocator);

			runJavascriptOnElement("arguments[0].click();", eledocumentType);
			uiGenericLocator = formatForLogging(uiGenericLocator);
			if (logging) {
				ExtentTestManager.getTest().log(LogStatus.PASS, "Clicked successfully on  " + uiGenericLocator,
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				LOGGER.info("Clicked successfully on  " + uiGenericLocator);
			} else {
				LOGGER.info("Clicked successfully on  " + uiGenericLocator);
			}

			waitForPageToSynchronizedWithServer(driver);
			//v waitForPageToFinishLoading(driver);
			break;
		case 2:
			uiElement = args[1];
			logging = true;
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			innerClick(logging, uiGenericLocator);
			break;
		case 3:
			uiElement = args[1];
			uiData = args[2];
			logging = true;
			uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
			uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
			innerClick(logging, uiGenericLocator);
			break;
		default:
			break;
		}

	}

	private String formatForLogging(String uiGenericLocator) {
		int rangeTo = uiGenericLocator.lastIndexOf("'");
		String trimmedSet1 = uiGenericLocator.substring(0, rangeTo);
		int rangeFrom = trimmedSet1.lastIndexOf("'");
		return uiGenericLocator.substring(rangeFrom, rangeTo + 1);
	}

	private void innerClick(Boolean logging, String uiGenericLocator) {
		WebElement targetElement = null;
		try {
			targetElement = findElementInner(uiGenericLocator);

			// isSynchronizedWithServer();
			if (targetElement.isDisplayed()) {
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetElement);
					try {
						runJavascriptOnElement("arguments[0].click();", targetElement);
						ExtentTestManager.getTest().log(LogStatus.PASS, "Element clicked sucessfully",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));

					} catch (Exception e) {
						LOGGER.warn("Exception in Syncwithserver: " + e.getMessage());
						ExtentTestManager.getTest().log(LogStatus.FAIL, "Unable to click on element ",
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
					waitForPageToSynchronizedWithServer(driver);
					//v waitForPageToFinishLoading(driver);

				} catch (Exception e) {
					LOGGER.warn("Exception in performing the scrollIntoView operation of findElementBy(): "
							+ e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Exception in find Element : " + e.getMessage());
		}

	}

	/**
	 * runJavascriptOnElement method is for executes specified java script on
	 * desired element.
	 * 
	 * @param script  the script
	 * @param element the element
	 */
	public void runJavascriptOnElement(String script, WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript(script, element);
		} catch (Exception ex) {
			LOGGER.error("Exception in executing JavaScript: " + script + " ERROR:\n", ex);
		}
	}

	public void selectFirstElement(String checkPresenceofPopup, String sortFactor, String primaryKey) {
		String FirstRow = "//span[contains(text(),'__LABEL__')]/ancestor::div[2]/following-sibling::div[1]//table//tr[1]";
		String sortLogic = "//span[.='__LABEL__']/../..//a[@title='Sort Descending']";
		String OKButtonForPopup = "(//span[text()='OK']/parent::a)[2]";
		try {

			isSynchronizedWithServer();
			driver.findElement(By.xpath(checkPresenceofPopup));// Popup Exists or not
			String firstTableEntry = getLocatorWithLabel(FirstRow, sortFactor);
			try {
				assertTrue(findElementInner(firstTableEntry).isDisplayed());
				// multi entry found.select first row
				String sortEntry = getLocatorWithLabel(sortLogic, "Effective Date");
				
				
				
				mouseoverandclick(sortEntry);
				// firstrow
				click(true, firstTableEntry);
				LOGGER.info("Multi Entry found for " + primaryKey);
				click(true, OKButtonForPopup);
				ExtentTestManager.getTest().log(LogStatus.PASS,
						"Multiple Entried found for '" + primaryKey + "' Selected 1st row",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
			}

			catch (Exception e) {
				click(true, OKButtonForPopup);
				LOGGER.error("Entry Not found for " + primaryKey);

				ExtentTestManager.getTest().log(LogStatus.FAIL, "Entry Not found for '" + primaryKey + "'",
						ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));

			}

		} catch (Exception e) {

		}

	}
	
	
	/***
	 * Replace __DATA__ string present in 1st arguement with 2nd arguement Asserts
	 * if element is present on the UI
	 * 
	 * @param uiGenericLocator
	 * @param testdata
	 */

	public void verifyEquals(Boolean logging, String... args) {
		String uiGenericLocator = args[0];
		String uiElement = null;
		String uiData = null;
		String testdata = null;
		String attributeRequested =null;
		String expectedValue = null;
		String textValFetched = null;
		int varargs = args.length;
		if (!(args[varargs - 1].equalsIgnoreCase("") || args[varargs - 1].equalsIgnoreCase("N/A") || args[varargs - 1].equalsIgnoreCase("NA"))) {
			switch (varargs) {
			case 1:// snap,xpath

				try {
					assertTrue(findElementInner(uiGenericLocator).isDisplayed());

					/* if (logging) { */
					ExtentTestManager.getTest().log(LogStatus.PASS, "Verified element presence successfully",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					/*
					 * } else { ExtentTestManager.getTest().log(LogStatus.PASS,
					 * "Verified element presence successfully"); }
					 */
				} catch (Exception e) {
					LOGGER.fatal("Element not found : " + uiGenericLocator);
					ExtentTestManager.getTest().log(LogStatus.FAIL, "Field : '" + uiGenericLocator + "' is not present",
							ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
				}

				break;
			case 3:// snap,xpath,data or //snap,xpath,lbl
					if (uiGenericLocator.contains("__LABEL__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
					}
					if (uiGenericLocator.contains("__DATA__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithData(uiGenericLocator, uiElement);
					}
					try {
						attributeRequested = args[1];
						expectedValue = args[2];
						WebElement eledocumentType = findElementInner(uiGenericLocator);
						textValFetched = eledocumentType.getAttribute(attributeRequested);
						assertEquals(textValFetched,expectedValue);
						
						if (logging) {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Expected value : '" + textValFetched + "' Found value : " + expectedValue,
									ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
						} else {
							ExtentTestManager.getTest().log(LogStatus.PASS,
									"Expected value : '" + textValFetched + "' Found value : " + expectedValue);
						}
					} catch (Exception e) {
						LOGGER.fatal("Element not found with value " + uiElement);
						ExtentTestManager.getTest().log(LogStatus.FAIL,
								"Expected value : '" + textValFetched + "' but found value : " + expectedValue,
								ExtentTestManager.getTest().addBase64ScreenShot(getSnapshot()));
					}
				

				break;
			case 4:// snap,xpath,lbl,data or //snap,xpath,lbl,data
				testdata = args[3];
				if (uiGenericLocator.contains("__LABEL__") || (uiGenericLocator.contains("__DATA__"))) {
					if (uiGenericLocator.contains("__LABEL__")) {
						uiElement = args[1];
						uiGenericLocator = getLocatorWithLabel(uiGenericLocator, uiElement);
					}
					if (uiGenericLocator.contains("__DATA__")) {
						uiData = args[2];
						uiGenericLocator = getLocatorWithData(uiGenericLocator, uiData);
					}
				}
				verifyElementWithData(logging, uiGenericLocator, testdata);
				break;

			default:
				break;
			}
		}
	}


}
