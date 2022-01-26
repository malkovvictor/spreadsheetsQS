package ru.victormalkov.reportchecker.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victormalkov.reportchecker.service.*;

public class ResultFormController {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    @FXML
    TextArea myTextArea;

    @FXML
    ProgressIndicator progressIndicator;

    public void initialize() {
        final String sellReportSpreadsheetId = ReportCheckerUI.sellReportSpreadsheetId;
        final String dailyReportSpreadsheetId = ReportCheckerUI.dailyReportSpreadsheetId;
        //final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";
        //final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DailySellReportReader sellReportReader = new DailySellReportReader();
                DailyReportReader dailyReportReader = new DailyReportReader();

                DailyReport report = sellReportReader.readReport(sellReportSpreadsheetId);
                DailyReport report2 = dailyReportReader.readReport(dailyReportSpreadsheetId);
                report.debugPrintKeys();
                report2.debugPrintKeys();
                StringBuilder text = new StringBuilder();
                for (Day d : report.getDays()) {
                    Day d2 = report2.getDay(d.getName());
                    text.append(d.toPrettyString(d2));
                }
                Platform.runLater(() -> myTextArea.appendText(text.toString()));
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }
        };
        new Thread(task).start();
    }
}
