package ru.victormalkov.reportchecker.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victormalkov.reportchecker.service.*;

import java.io.IOException;

public class ResultFormController {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    @FXML
    TextArea myTextArea;

    @FXML
    ProgressIndicator progressIndicator;

    public void initialize() {
        final String sellReportSpreadsheetId = ReportCheckerUI.sellReportSpreadsheetId;
        final String dailyReportSpreadsheetId = ReportCheckerUI.dailyReportSpreadsheetId;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }

            @Override
            protected void failed() {
                super.failed();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Проверка отчётов");
                alert.setHeaderText(null);
                alert.setContentText("Что-то пошло не так.");
                alert.showAndWait();
                Platform.exit();
            }
        };
        new Thread(task).start();
    }
}
