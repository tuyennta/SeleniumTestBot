package com.tuyennta.automation.bot;

import com.tuyennta.automation.dto.TestScript;
import com.tuyennta.automation.utils.ExcelHelper;

public class AutomateBot {

	public static boolean generateScript () {
		ExcelHelper excelHelper = new ExcelHelper();
		excelHelper.writeToFile(TestScript.getInstance().getListStep(), "TestScript.xlsx", "F:\\works\\upskills\\TestAutomation\\TestScript\\xxx.xlsx");
		
		//clean test script
		TestScript.getInstance().removeAllSteps();
		return true;
	}
}
