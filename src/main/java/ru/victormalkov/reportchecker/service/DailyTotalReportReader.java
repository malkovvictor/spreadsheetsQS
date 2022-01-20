package ru.victormalkov.reportchecker.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class DailyTotalReportReader {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    public DailyReport readReport(String spreadsheetId) throws IOException {
        DailyReport result = new DailyReport();
        Sheets sheetsService = AuthUtil.getSheetsService();

        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
                .setIncludeGridData(true)
                .execute();
        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet sheet : sheets) {

        }

        return result;
    }
}
