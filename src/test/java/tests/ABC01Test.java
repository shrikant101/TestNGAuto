package tests;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import pages.BasePage;
import pages.HomePage;
import utils.ExtentReports.ExtentTestManager;

public class ABC01Test extends BaseTest {

	private static final Logger LOGGER = Logger.getLogger(ABC01Test.class);
	
	// *************WEB ELEMENTS********************** }
	//
	@Test(priority = 0, description = "Login to OBP and navigate to FastPath")
	public void loginOBP(Method method) throws InterruptedException {
		ExtentTestManager.startTest(method.getName(), "Login to OBP and navigate to FastPath");
		try {
			// *************PAGE INSTANTIATIONS*************
			HomePage homePage = new HomePage(driver, wait);

			// *************PAGE METHODS********************
			// Open OBP HomePage -- > login to OBP -- > nagivate fastpath			
			homePage.launchBrowser();
			homePage.goToLoginPage();
			homePage.navigatetoFastPath("ABC01");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(dataProvider = "superman", priority = 1, description = "Start verification of ABC01 inbound screen")
	public void ABC01_Inbound(Method method, String NewDocumentType, String DocumentTypeDescription, String IdentificationCategoryOld, 
			String IdentificationCategory, String DocumentTypeClassification, String ExpiryTrackingMethod, String Period_YY, 
			String Period_MM, String Period_DD, String IssueDateAliasExpiryDateAlias, String ScrutinyMode,String PrimaryIndexType) 
					throws InterruptedException {

		// ExtentReports Description
		ExtentTestManager.startTest(NewDocumentType, "Start verification of ABC01 inbound screen");
	
		// *************PAGE INSTANTIATIONS*************
		BasePage basepage = new BasePage(driver, wait);

		// *************PAGE ELEMENTS********************	
		String fdyearCheck = "(//label[.='Period']/../following-sibling::td//span)[2]";
		String fdmonthCheck = "(//label[.='Period']/../following-sibling::td//span)[3]";
		String fddayCheck = "(//label[.='Period']/../following-sibling::td//span)[4]";
		
		// *************PAGE METHODS********************		
		basepage.readyAUT(false,locatorProp.getProperty("searchLink"),locatorProp.getProperty("clearConfig"));
		basepage.enter(true,locatorProp.getProperty("inputFieldL1"),"Document Type",NewDocumentType);
		basepage.clickusingjs(true,locatorProp.getProperty("searchLink"));
		
		//multi entry
		//basepage.selectFirstElement(locatorProp.getProperty("closeSearchBox"),"Document Type",NewDocumentType);
		
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type",NewDocumentType);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type Description",DocumentTypeDescription);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Identification Category",IdentificationCategory);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type Classification",DocumentTypeClassification);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Expiry Tracking Method",ExpiryTrackingMethod);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Scrutiny Mode",ScrutinyMode);
		
		basepage.verifyEquals(true,fdyearCheck,"innerText",Period_YY);
		basepage.verifyEquals(true,fdmonthCheck,"innerText",Period_MM);
		basepage.verifyEquals(true,fddayCheck,"innerText",Period_DD);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Issue Date Alias",IssueDateAliasExpiryDateAlias);
		
		basepage.verifyElementWithDataAndValue(true,locatorProp.getProperty("tableIndexType"),"Primary Index Type",PrimaryIndexType);
		
		basepage.click(false,locatorProp.getProperty("clearConfig"));
		
		}
	
	
	@Test(dataProvider = "superman", priority = 2, description = "Start verification of ABC01 outbound screen")
	public void ABC01_Outbound(Method method, String DocumentType, String DocumentTypeDescription, 
			String DocumentTypeClassification, String PermissibleDeliveryChannel_1, String DefaultDeliveryChannel, String AcceptanceRequired, 
			String ProtectWithPassword, String Mandatory, String ExecutionRequired, String UploadableVersionAllowed, 
			String ReferenceNumberValidationRequired,String IndexType_1, String InbounddocumentInsert) 
					throws InterruptedException {

		// ExtentReports Description
		ExtentTestManager.startTest(DocumentType, "Start verification of ABC01 outbound screen");
	
		// *************PAGE INSTANTIATIONS*************
		BasePage basepage = new BasePage(driver, wait);

		// *************PAGE ELEMENTS********************	
		String fdTable = "//span[.='__LABEL__']";
		// *************PAGE METHODS********************		
		basepage.readyAUT(false,locatorProp.getProperty("searchLink"),locatorProp.getProperty("clearConfig"));
		basepage.enter(true,locatorProp.getProperty("inputFieldL1"),"Document Type",DocumentType);
		basepage.clickusingjs(true,locatorProp.getProperty("searchLink"));
		
		//multi entry
		//basepage.selectFirstElement(locatorProp.getProperty("closeSearchBox"),"Document Type",NewDocumentType);
		
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type",DocumentType);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type Description",DocumentTypeDescription);
		basepage.verifyElementPresent(true,locatorProp.getProperty("lblGenericFieldL2"),"Document Type Classification",DocumentTypeClassification);
		
		basepage.verifyCheckBox(true,locatorProp.getProperty("chkField2Level"), "Protect With Password", ProtectWithPassword);
		basepage.verifyCheckBox(true,locatorProp.getProperty("chkField2Level"), "Acceptance Required", AcceptanceRequired);
		basepage.verifyCheckBox(true,locatorProp.getProperty("chkField2Level"), "Mandatory", Mandatory);
		basepage.verifyCheckBox(true,locatorProp.getProperty("chkField2Level"), "Execution Required", ExecutionRequired);
		basepage.verifyCheckBox(true,locatorProp.getProperty("chkField2Level"), "Reference Number Validation Required", ReferenceNumberValidationRequired);
		
		basepage.verifyElementWithDataAndValue(true,locatorProp.getProperty("tableIndexType"),"Primary Index Type",IndexType_1);
		basepage.verifyElementWithData(true,fdTable,InbounddocumentInsert);
		
		basepage.click(false,locatorProp.getProperty("clearConfig"));
		
		}

}
