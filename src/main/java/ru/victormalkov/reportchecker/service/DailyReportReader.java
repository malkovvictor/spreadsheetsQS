package ru.victormalkov.reportchecker.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class DailyReportReader {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    public DailyReport readReport(String spreadsheetId) throws IOException {
        DailyReport report = new DailyReport();
        Sheets sheetsService = AuthUtil.getSheetsService();

        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
                .setIncludeGridData(true)
                .execute();
        List<Sheet> sheets = spreadsheet.getSheets();

        for (Sheet sheet : sheets) {
            logger.info("Sheet: " + sheet.getProperties().getTitle());
            logger.info("id: " + sheet.getProperties().getSheetId());

            Day d = new Day();
            d.setName(sheet.getProperties().getTitle());
            GridData data = sheet.getData().get(0);
            List<RowData> rData = data.getRowData();

            d.addDayCache(readValue(rData, 2, 1));
            d.addDayOnline(readValue(rData, 2, 2));
            d.addDayTerminal(readValue(rData, 2, 3));

            d.addNightCache(readValue(rData, 2, 9));
            d.addNightOnline(readValue(rData, 2, 10));
            d.addNightTerminal(readValue(rData, 2, 11));

            report.pushDay(d);
        }

        return report;
    }

    private int readValue(List<RowData> rdata, int c, int r) {
        logger.info(String.format("Column: %d, Row: %d, value: %s", c, r, rdata.get(r).getValues().get(c).getFormattedValue()));
        try {
            return Integer.parseInt(rdata.get(r).getValues().get(c).getFormattedValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}