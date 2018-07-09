package com.tuyennta.automation.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.tuyennta.automation.dto.TestStep;

public class ExcelHelper {

	public void writeToFile(List<TestStep> listStep, String resourceName, String fileOutLocation) {
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(getClass().getClassLoader().getResourceAsStream(resourceName));
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sheet sheet = workbook.getSheetAt(0);
		int rowIndex = 1;
		int cellIndex = 0;
		for (TestStep step : listStep) {
			Row row = sheet.createRow(rowIndex++);
			row.createCell(cellIndex++).setCellValue("r");
			row.createCell(cellIndex++).setCellValue(step.getAction());
			row.createCell(cellIndex++).setCellValue(step.getXpath());
			row.createCell(cellIndex).setCellValue(step.getParameters());
			cellIndex = 0;
		}

		File fileOut = new File(fileOutLocation);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileOut);
			workbook.write(fos);
			workbook.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
