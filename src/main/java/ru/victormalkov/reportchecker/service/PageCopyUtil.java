package ru.victormalkov.reportchecker.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;

import java.io.IOException;

public class PageCopyUtil {
    public static void copy1Page(String spreadsheetId) throws IOException {
        Sheets sheetsService = AuthUtil.getSheetsService();
//        Drive driveService = AuthUtil.getDriveService();
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new  BatchUpdateSpreadsheetRequest();
        DuplicateSheetRequest requestBody = new DuplicateSheetRequest();
        requestBody.setNewSheetName("test");
        requestBody.setSourceSheetId(0);
    }
}
