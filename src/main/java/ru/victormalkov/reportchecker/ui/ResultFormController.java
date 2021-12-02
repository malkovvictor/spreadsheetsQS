package ru.victormalkov.reportchecker.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import ru.victormalkov.reportchecker.service.DailyReport;
import ru.victormalkov.reportchecker.service.DailySellReportReader;
import ru.victormalkov.reportchecker.service.Day;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ResultFormController {
    @FXML
    TextArea myTextArea;

    @FXML
    Label labelIsCounting;

    public void initialize() throws IOException, GeneralSecurityException {
        final String spreadsheetId = ReportCheckerUI.spreadsheetId;
        //final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";
        //final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                DailySellReportReader sellReportReader = new DailySellReportReader();
                DailyReport report = sellReportReader.readReport(spreadsheetId);
                StringBuilder text = new StringBuilder();
                for (Day d : report.getDays()) {
                    text.append(d.toPrettyString());
                }
                Platform.runLater(() -> myTextArea.appendText(text.toString()));
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> labelIsCounting.setVisible(false));
            }
        };
        new Thread(task).start();
    }
}
