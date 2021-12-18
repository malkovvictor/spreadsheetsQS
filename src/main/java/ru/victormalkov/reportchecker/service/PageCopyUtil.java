package ru.victormalkov.reportchecker.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageCopyUtil {
    private static final Pattern datePattern = Pattern.compile("(\\d+)[\\.,](\\d+)[\\.,](\\d+)");

    /**
     *
     * @param spreadsheetId
     * @return Number of created pages
     * @throws IOException
     */
    public static int copy1Page(String spreadsheetId) throws IOException {
        Sheets sheetsService = AuthUtil.getSheetsService();
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
                .setIncludeGridData(true)
                .execute();

        List<Sheet> sheets = spreadsheet.getSheets();

        if (sheets.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Создание страниц");
            alert.setHeaderText("Ошибочка вышла!");
            alert.setContentText("Не найдено ни одного листа, нечего копировать");
            alert.showAndWait();
            return 0;
        } else if (sheets.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Создание страниц");
            alert.setHeaderText("Ошибочка вышла!");
            alert.setContentText("В таблице есть несколько листов - непонятно, что копировать");
            alert.showAndWait();
            return 0;
        }
        Integer sheetId = sheets.get(0).getProperties().getSheetId();
        String name = sheets.get(0).getProperties().getTitle();
        Matcher m = datePattern.matcher(name);
        if (!m.find())  {
            return 0;
        }
        System.out.println(m.groupCount());
        if (m.groupCount() != 3) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Создание страниц");
            alert.setHeaderText("Ошибочка вышла!");
            alert.setContentText("Непонятное название листа");
            alert.showAndWait();
            return 0;
        }
        int month = Integer.parseInt(m.group(2));
        int year = Integer.parseInt(m.group(3));
        int lengthOfMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int i = 2; i <= lengthOfMonth; i++) {
            DuplicateSheetRequest requestBody = new DuplicateSheetRequest();
            requestBody.setNewSheetName((i < 10 ? "0" : "") + i + "." + (month < 10 ? "0" : "") + month + "." + year);
            requestBody.setSourceSheetId(sheetId);
            requestBody.setInsertSheetIndex(i);
            requests.add(new Request().setDuplicateSheet(requestBody));
        }

        batchUpdateSpreadsheetRequest.setRequests(requests);
        Sheets.Spreadsheets.BatchUpdate request = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);

        BatchUpdateSpreadsheetResponse response = request.execute();

        return lengthOfMonth - 1;
    }
}
