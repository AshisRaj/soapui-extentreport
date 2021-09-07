package com.araj.soapui.listener;

import com.araj.manager.CSVReportManager;
import com.araj.manager.ExtentReportManager;
import com.araj.manager.PropertyManager;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;

/**
 * 
 * @author Ashis.Raj
 *
 */
public class TestCaseRunListener extends TestRunListenerAdapter {
	
	private static PropertyManager propertyManager;
	private long startTime;
		
	static {
		SoapUI.log("Initializing PropertyManager, ExtentReportManager and CSVReportManager...");
		propertyManager = PropertyManager.getInstance();
		ExtentReportManager.getInstance();
		CSVReportManager.getInstance();
		SoapUI.log("Done.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.eviware.soapui.model.support.TestRunListenerAdapter#beforeRun(com.eviware.soapui.model.testsuite.TestCaseRunner, com.eviware.soapui.model.testsuite.TestCaseRunContext)
	 */
	public void beforeRun(TestCaseRunner testRunner, TestCaseRunContext runContext) {
		startTime = System.nanoTime();
//		String skipTestSuitesList = propertyManager.getValueForKey("skipTestSuites");
//		if(!skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())
//			&& !skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())
//			&& !skipTestSuitesList.contains(testRunner.getTestCase().getTestSuite().getName())) {
//
//			SoapUI.log("CurrentStep and Index - " + runContext.getCurrentStep().getName() + ", " + runContext.getCurrentStepIndex());
//			SoapUI.log("Property Value of Test case ID - " + testRunner.getTestCase().getPropertyValue("Test Case ID"));
			ExtentReportManager.startExtentTest(testRunner, runContext);
//			CSVReportManager.startCSVReport(testRunner);
//		}
	}
 
	/*
	 * (non-Javadoc)
	 * @see com.eviware.soapui.model.support.TestRunListenerAdapter#afterRun(com.eviware.soapui.model.testsuite.TestCaseRunner, com.eviware.soapui.model.testsuite.TestCaseRunContext)
	 */ 
	public void afterRun(TestCaseRunner testRunner, TestCaseRunContext runContext) {
//		ExtentReportManager.extentReport(testRunner, runContext);
//		CSVReportManager.csvReport(testRunner);
		long endTime = System.nanoTime();
//		SoapUI.log( "TestCase [" + testRunner.getTestCase().getName() + "] took " + (endTime-startTime) + 
//	        " nanoseconds." );
//		SoapUI.log( "TestCase [" + testRunner.getTestCase().getName() + "] has " + testRunner.getResults().size() + 
//	        " steps executed." );
	}
	
	public void beforeStep(TestCaseRunner testRunner, TestCaseRunContext runContext, TestStep testStep) {
		
	}
 
	/*
	 * (non-Javadoc)
	 * @see com.eviware.soapui.model.support.TestRunListenerAdapter#afterStep(com.eviware.soapui.model.testsuite.TestCaseRunner, com.eviware.soapui.model.testsuite.TestCaseRunContext, com.eviware.soapui.model.testsuite.TestStepResult)
	 */ 
	public void afterStep(TestCaseRunner testRunner, TestCaseRunContext runContext, TestStepResult testStepResult) {
		// CSVReportManager.csvReport(testRunner,testStepResult);
		 ExtentReportManager.extentReport(testRunner, testStepResult);
	}
}