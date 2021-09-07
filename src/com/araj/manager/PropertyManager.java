package com.araj.manager;

import java.io.InputStream;
import java.util.Properties;

import com.eviware.soapui.SoapUI;

public class PropertyManager {
	public static PropertyManager _instance = null;
	public Properties testData;
	InputStream fileConfig = null;
	
	public PropertyManager() {		 
		try {
			testData = new Properties();
			fileConfig = PropertyManager.class.getResourceAsStream("/resources/application.properties");
			if (fileConfig != null) {
				testData.load(fileConfig);
				SoapUI.log("Loaded application properties file");
				fileConfig.close();
			}
			else {
				SoapUI.log("PropertyManager Error on reading config file");
			}        
		}
		catch (Exception e) {
			SoapUI.logError(e);
        }   
	}
		
	public static PropertyManager getInstance() {
		if (_instance == null) {
			_instance = new PropertyManager();
		}
		return _instance;
    }

	public String getValueForKey(String key) {
		return testData.getProperty(key);
	}
}

