package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends BasePage {

    //*********Constructor*********
    public HomePage (WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    //*********Page Variables*********
    String baseURL = "https://google.com/main.jspx";
    //String baseURL = "https://google.com/main.jspx";
    //String baseURL = "https://google.com/main.jspx";

    String basePMURL = "https://google.com/main.jspx";
 // *************PAGE INSTANTIATIONS*************
 		BasePage basepage = new BasePage(driver, wait);

    //*********Web Elements*********
    String signInButtonClass = "btnSignIn";
	String userName = "//input[@name='username' or @name='j_username' or @placeholder='Username']";
	String password="//input[@name='password' or @name='j_password' or @placeholder='Password']";
	String LoginOBP= "//*[@value='Sign In' or text()='Sign in']";
	
	
	String fastpathForConfig = "//INPUT[@id='pt1:fastPathSubForm:it4::content']";
	String fastpathSearch= "//IMG[@id='pt1:fastPathSubForm:cil2::icon']";


    //*********Page Methods*********

    //Go to Homepage
    public void launchBrowser (){
        driver.get(baseURL);
    }
    
    public void launchPMBrowser (){
        driver.get(basePMURL);
    }

    //Go to LoginPage
    public void goToLoginPage (){
    	
    	driver.findElement(By.xpath(userName)).sendKeys("testuser23");
		driver.findElement(By.xpath(password)).sendKeys("welcome1");
		driver.findElement(By.xpath(LoginOBP)).click();
    }

	public void navigatetoFastPath(String strFastPath) throws InterruptedException {
		basepage.enter(true,fastpathForConfig,strFastPath);
		isSynchronizedWithServer();
		basepage.click(true,fastpathSearch);
		isSynchronizedWithServer();

	}

}
