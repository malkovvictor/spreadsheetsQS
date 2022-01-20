package ru.victormalkov.reportchecker.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import ru.victormalkov.reportchecker.service.DailyReport;
import ru.victormalkov.reportchecker.service.DailySellReportReader;
import ru.victormalkov.reportchecker.service.Day;

public class ResultFormController {
    @FXML
    WebView myTextView;

    @FXML
    ProgressIndicator progressIndicator;

    public void initialize() {
        final String spreadsheetId = ReportCheckerUI.spreadsheetId;
        //final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";
        //final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DailySellReportReader sellReportReader = new DailySellReportReader();
                DailyReport report = sellReportReader.readReport(spreadsheetId);
                StringBuilder text = new StringBuilder("<html><body><table>");
                for (Day d : report.getDays()) {
                    text.append(d.toHTML());
                }
                //Platform.runLater(() -> myTextArea.  .appendText(text.toString()));
                text.append("</table></body></html>");
                Platform.runLater(() -> myTextView.getEngine().loadContent(text.toString()));
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
