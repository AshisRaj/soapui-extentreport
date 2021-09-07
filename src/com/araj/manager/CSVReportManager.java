package com.araj.manager;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public class CSVReportManager {
	private static CSVReportManager _instance = null;
	private static String csvReportFolderPath = null;
	private static FileWriter fileWriter = null;
    private static String fileSeperator = System.getProperty("file.separator");
    private static PropertyManager propertyManager = PropertyManager.getInstance();
   
	public CSVReportManager() {		 
		csvReportFolderPath = getReportPath();
        SoapUI.log("Report Path - " + csvReportFolderPath);
	}
		
	public static CSVReportManager getInstance() {
		if (_instance == null) {
			_instance = new CSVReportManager();
		}
		return _instance;
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
		String csvReportFolderPath = reportFolderPath + fileSeperator + "CSVReport";
		File extentReport = new File(csvReportFolderPath);
		if(!extentReport.exists()) {
			extentReport.mkdirs();
		}
		return csvReportFolderPath;
    } 

    public static void startCSVReport(TestCaseRunner testRunner) {
    	String skipTestSuitesList = propertyManager.getValueForKey("skipTestSuites");
		if(!skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())) {
			try {
				// Retrieve Test Suite/Feature name
				String featureName = testRunner.getTestCase().getTestSuite().getName();
				SoapUI.log("Feature Name - " + featureName);
				// Retrieve Test Case/User Story name
				String userStory = testRunner.getTestCase().getName();
				SoapUI.log("User Story - " + userStory);
				// Retrieve Test Case ID
				String testCaseID = testRunner.getTestCase().getPropertyValue("Test Case ID");
				SoapUI.log("\tTest Case ID - " + testCaseID);
				/* ------------------------------------------------------------------------------- */
				// 2. Create other subfolders (feature and user story) to store the reports file 
				// Specify the subfolder path with Feature Name
				String featureFolderPath = csvReportFolderPath + fileSeperator + featureName;
				// Create a File object for the specified path
				File featureRsultFolder = new File(featureFolderPath);
				// Check for existence of folder and create a folder
				if(!featureRsultFolder.exists()) {
					featureRsultFolder.mkdirs();
				}
				// Specify the subfolder path with User Story name
				String userStoryFolderPath = featureFolderPath + fileSeperator + userStory;
				// Create a File object for the specified path
				File userStoryResultFolder = new File(userStoryFolderPath);
				// Check for existence of folder and create a folder
				if(!userStoryResultFolder.exists()) {
					userStoryResultFolder.mkdirs();
				}
				/* ------------------------------------------------------------------------------- */
				// 3. Create a Report.csv file inside the CSV Reports folder 
				// Create a File object for Report csv file (with timestamp)
				SimpleDateFormat dateFormat = new SimpleDateFormat(propertyManager.getValueForKey("dateFormatStr"));
//				SoapUI.log("Date pattern - " + dateFormat.toPattern());
				Date d = new Date();
				String executionDate = dateFormat.format(d);
				
				File reportFile = new File(userStoryResultFolder, userStory.replaceAll("\\s","") + "_Report_"+executionDate+".csv");
				// Check for existence of report file and create a file
				if(!reportFile.exists()) {
					reportFile.createNewFile();

					// Create required column names in the report file
					fileWriter = new FileWriter(reportFile);
					fileWriter.write("\"Feature\",\"User Story\",\"Test Case ID\",\"Status\",\"Messages\",\"Execution Date\"");
					fileWriter.flush();
				}
			}
			catch(Exception e) {
				SoapUI.logError(e);
			}
		}
    }

	public static void csvReport(TestCaseRunner testRunner) {
		for(TestStepResult testStepResult : testRunner.getResults()) {	
			csvReport(testRunner, testStepResult);
		}
	}
	
	public static void csvReport(TestCaseRunner testRunner, TestStepResult testStepResult) {
		String skipTestSuitesList = propertyManager.getValueForKey("skipTestSuites");
		if(!skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())) {
			// Retrieve Test Suite/Feature name
			String featureName = testRunner.getTestCase().getTestSuite().getName();
//			SoapUI.log("Feature Name - " + featureName);
			// Retrieve Test Case/User Story name
			String userStory = testRunner.getTestCase().getName();
//			SoapUI.log("User Story - " + userStory);
			// Retrieve Test Case ID
			String testCaseID = testRunner.getTestCase().getPropertyValue("Test Case ID");
			SoapUI.log("\tTest Case ID - " + testCaseID);
			
			try {
				/* ------------------------------------------------------------------------------- */
				// 4. Inserting data in the file
				// Iterate over all the test steps results
				boolean tcFailed = false;
				StringBuilder sb = null;
				TestStep testStep = testStepResult.getTestStep();
				// Retrieve Test Step name
				String testStepName = testStep.getName();
				// Retrieve Test Step status
				TestStepStatus status = testStepResult.getStatus();
			
				if("FAILED".equalsIgnoreCase(status.toString()) || "FAIL".equalsIgnoreCase(status.toString())) {
					tcFailed = true;
					// Retrieve the test result messages
					sb = new StringBuilder();
					for(String resMessage : testStepResult.getMessages()) {
						// Write messages and separate multiple messages by new line
						sb.append("Message:" + resMessage + "\n");
					}
				}
				// Creating new line in report file
				fileWriter.append('\n');
				// Write all the necessary information in the file
				fileWriter.append("\"" + featureName + "\",").append("\"" + userStory + "\",");
				fileWriter.append("\"" + testCaseID + "\",");
				fileWriter.append("\"" + (tcFailed? "FAILED" : "PASSED") + "\",");
				fileWriter.append("\"");
				fileWriter.append(sb == null? "" : sb.toString() + '\n');
				fileWriter.append("\",");
				SimpleDateFormat dateFormat = new SimpleDateFormat(propertyManager.getValueForKey("dateFormatStr"));
				Date d = new Date();
				String executionDate = dateFormat.format(d);
				//Write executionDate in the file
				fileWriter.append("\"" + executionDate + "\",");
				fileWriter.flush();
				//fileWriter.close();
			}
			catch(Exception e) {
				SoapUI.logError(e);
			}
		}
	}	
}
