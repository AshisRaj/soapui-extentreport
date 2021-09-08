# soapui-extentreport #
A ReadyAPI Listener Plugin for Extent Report generation. The plugin listens to the events in the background as you run the Test Cases (related Features and Stories) from ReadyAPI. 

Find and download the jar and xml file with steps below to set it up. Please note that the plugin is in beta and a couple of trivial issues identified need to fixed.

1. Download soapui-extentreport.jar and put it inside bin\ext folder of your ReadyAPI install folder. e.g. C:\Program Files\SmartBear\ReadyAPI-3.9.1\bin\ext
2. Download testcaserun-listeners.xml from Resources folder and put it inside bin\listeners folder of your ReadyAPI install folder. e.g. C:\Program Files\SmartBear\ReadyAPI-3.9.1\bin\listeners
4. Open the jar file (soapui-extentreport.jar) using 7zip like tool and Browse to resources folder ==> Directly open, edit and save the application.properties file with
    1. change the folder path of reportFolderPath property with path of your choice where you want the ExtentReport html file to be written). Take care of double backslashes. e.g. reportFolderPath=C:\\Users\\ashis.raj\\ReadyAPI\\TestReports\
    2. Add/Change the Name of the Test Suites that you do not want to generate the extent report for. Separate each Test Suite Names with a comma.
5. Restart the ReadyAPI and see if you find the below like log message in RedyAPILog tab.

Tue Dec 10 13:03:52 IST 2019: INFO: Adding listeners from [C:\Program Files\SmartBear\ReadyAPI-3.9.1\bin\listeners\testcaserun-listeners.xml]

Initializing PropertyManager, ExtentReportManager and CSVReportManager...

Loaded application properties file

Report Path - C:\Users\ashis.raj\ReadyAPI\TestReports\CSVReport

Done.

Tue Dec 10 13:03:52 IST 2020: INFO: Adding listener [class com.araj.soapui.listener.TestCaseRunListener]
