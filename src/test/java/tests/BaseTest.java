package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import pages.BasePage;
public class BaseTest {
    public WebDriver driver;
    public WebDriverWait wait;
	public static XSSFWorkbook workbook;
	public static XSSFSheet worksheet;
	public static DataFormatter formatter = new DataFormatter();
	public static Properties locatorProp;
	File file;
    public WebDriver getDriver() {
        return driver;
    }

    
    
    @BeforeClass
    public void setup () {
    	BasePage.intiateLoggers();
        //Create a Chrome driver. All test classes use this.
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        //Create a wait. All test classes use this.
        wait = new WebDriverWait(driver,15);
        locatorProp = new Properties();
        file = new File("locator.properties");
        try {
			locatorProp.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //Maximize Window
        //driver.manage().window().maximize();
    }

    @AfterClass
    public void teardown () {
        //xperdriver.quit();
    }


	@DataProvider(name = "superman")
	public static Object[][] ReadVariant(Method method) {

		FileInputStream fileInputStream;
		try {
			String s = method.getName();
			System.out.println("Name found is :" + s);
			String file_location = System.getProperty("user.dir") + "/InputData/"+method.getName()+".xlsx";
			fileInputStream = new FileInputStream(file_location);
			workbook = new XSSFWorkbook(fileInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // get my workbook
		worksheet = workbook.getSheetAt(0);// get my sheet from workbook
		XSSFRow Row = worksheet.getRow(0); // get my Row which start from 0

		int RowNum = worksheet.getPhysicalNumberOfRows();// count my number of Rows
		int ColNum = Row.getLastCellNum(); // get last ColNum

		Object Data[][] = new Object[RowNum - 1][ColNum]; // pass my count data in array

		for (int i = 0; i < RowNum - 1; i++) // Loop work for Rows
		{
			XSSFRow row = worksheet.getRow(i + 1);

			for (int j = 0; j < ColNum; j++) // Loop work for colNum
			{
				if (row == null)
					Data[i][j] = "";
				else {
					XSSFCell cell = row.getCell(j);
					if (cell == null)
						Data[i][j] = ""; // if it get Null value it pass no data
					else {
						String value = formatter.formatCellValue(cell);
						Data[i][j] = value; // This formatter get my all values as string i.e integer, float all type
											// data value
					}
				}
			}
		}

		return Data;
	}

}
