package ru.victormalkov.reportchecker.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import ru.victormalkov.reportchecker.service.DailyReport;
import ru.victormalkov.reportchecker.service.DailySellReportReader;
import ru.victormalkov.reportchecker.service.Day;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ResultFormController {
    @FXML
    private TextArea myTextArea;

    public void initialize() throws IOException, GeneralSecurityException {
        String spreadsheetId = ReportCheckerUI.spreadsheetId;
        //final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";

        //final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";

        DailySellReportReader sellReportReader = new DailySellReportReader();
        DailyReport report = sellReportReader.readReport(spreadsheetId);
        for (Day d : report.getDays()) {
            myTextArea.appendText(d.toPrettyString());
        }

        myTextArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
}
