package com.araj.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public class ExtentReportManager {
    private static ExtentReports extentReports;
    private static ExtentTest extentTest;
    private static String fileSeperator = System.getProperty("file.separator");
    private static PropertyManager propertyManager = PropertyManager.getInstance();
    private static final String HTML_CONFIG = "/resources/html-config.xml";
   
    public static ExtentReports getInstance() {
        if (extentReports == null)
            createInstance();
        return extentReports;
    }
 
    //Create an extent report instance
    public static ExtentReports createInstance() {
        String fileName = getReportPath();
        SoapUI.log("Report Path - " + fileName);
        		
		SimpleDateFormat dateFormat = new SimpleDateFormat(propertyManager.getValueForKey("dateFormatStr"));
		Date d = new Date();
		String executionDate = dateFormat.format(d);
		ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(fileName + fileSeperator + "ExtentReport-" + executionDate + ".html");
		SoapUI.log("ExtentHtmlReporter file path - " + extentHtmlReporter.getFilePath());
		//htmlReporter.setAppendExisting(true);
		extentHtmlReporter.setAnalysisStrategy(AnalysisStrategy.TEST);

		InputStream srcHtmlConfigFile = null;
				
		try {
			srcHtmlConfigFile = ExtentReportManager.class.getResourceAsStream(HTML_CONFIG);
			if (srcHtmlConfigFile != null) {
				File targetFile = new File(System.getProperty("java.io.tmpdir") + fileSeperator + "targeHtmlConfigFile.xml");
				targetFile.delete();
				Files.copy(srcHtmlConfigFile, targetFile.toPath());
				extentHtmlReporter.loadXMLConfig(targetFile, false);
				srcHtmlConfigFile.close();
			}
			else {
				SoapUI.log("Error on readingHTML Config file for ExtentReporte");
			}
		} catch (IOException e) {
			SoapUI.logError(e);
		}
		
		extentReports = new ExtentReports();
		extentReports.attachReporter(extentHtmlReporter);
		extentReports.setSystemInfo("user", System.getProperty("user.name"));
		extentReports.setSystemInfo("Time Zone", System.getProperty("user.timezone"));
		extentReports.setSystemInfo("Machine", String.format("%s %s %s", 
			System.getProperty("os.name"),
			System.getProperty("os.version"),
			System.getProperty("os.arch")));
		extentReports.setSystemInfo("Machine Name : ", System.getProperty("machine.name"));
		extentReports.setSystemInfo("ReadyAPI Version", propertyManager.getValueForKey("ReadyAPIVersion"));
		extentReports.setSystemInfo("SOAPUIPro Version", propertyManager.getValueForKey("SOAPUIProVersion"));
		        
        return extentReports;
    }
     
	public static void startExtentTest(TestCaseRunner testRunner, TestCaseRunContext runContext ) {
		String skipTestSuitesList = propertyManager.getValueForKey("skipTestSuites");
		if(!skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())) {
			extentTest = extentReports.createTest(testRunner.getTestCase().getName(), testRunner.getTestCase().getDescription());
			extentTest.assignCategory(testRunner.getTestCase().getTestSuite().getName());
		}
	}
	
	public static void extentReport(TestCaseRunner testRunner, TestStepResult testStepResult) {
		ExtentTest extentTestChild = null;
		String skipTestSuitesList = propertyManager.getValueForKey("skipTestSuites");
		if(!skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())) {
			TestStep testStep = testStepResult.getTestStep();
			TestStepStatus status = testStepResult.getStatus();
			extentTestChild = extentTest.createNode(testStep.getName());
			StringBuilder sb = null;
			if("FAILED".equalsIgnoreCase(status.name()) || "FAIL".equalsIgnoreCase(status.name())) {
				// Retrieve the test result messages
				sb = new StringBuilder();
				for(String msg : testStepResult.getMessages()) {
					// Write messages and separate multiple messages by new line
					sb.append("Message:" + msg + "\n");
				}
				//extentTest.fail(testStep.getName());
				extentTestChild.fail(MarkupHelper.createCodeBlock(sb.toString()));
			}
			else {
				extentTestChild.pass(testStep.getName());
			}
		}
		extentReports.flush();
	}
	
	public static void extentReport(TestCaseRunner testRunner, TestCaseRunContext runContext) {
		for(TestStepResult testStepResult : testRunner.getResults()) {	
			extentReport(testRunner, testStepResult);
		}
		extentReports.flush();
	}
    
    //Create the report path
    private static String getReportPath() {
		// 1. Create a "reportFolderPath" folder in the project path
		
		// Create a File object for the specified path
		String reportFolderPath = propertyManager.getValueForKey("reportFolderPath");
		File reportFolder = new File(reportFolderPath);
		// Check for existence of folder and create a folder
		if(!reportFolder.exists()) {
			reportFolder.mkdirs();
		}

		// 2. Create another subfolder "Extent Report" to store the reports file
		
		// Create this sub-folder
		String extentReportFolderPath = reportFolderPath + fileSeperator + "ExtentReport";
		File extentReport = new File(extentReportFolderPath);
		if(!extentReport.exists()) {
			extentReport.mkdirs();
		}
		return extentReportFolderPath;
    } 
}
