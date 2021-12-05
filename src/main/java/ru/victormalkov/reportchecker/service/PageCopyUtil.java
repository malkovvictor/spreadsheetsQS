package ru.victormalkov.reportchecker.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;
import com.google.api.services.sheets.v4.model.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageCopyUtil {
    public static void copy1Page(String spreadsheetId) throws IOException {
        Sheets sheetsService = AuthUtil.getSheetsService();
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
        DuplicateSheetRequest requestBody = new DuplicateSheetRequest();
        requestBody.setNewSheetName("test");
        requestBody.setSourceSheetId(0);

        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setDuplicateSheet(requestBody));
        batchUpdateSpreadsheetRequest.setRequests(requests);
        Sheets.Spreadsheets.BatchUpdate request = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);

        BatchUpdateSpreadsheetResponse response = request.execute();
    }
}
