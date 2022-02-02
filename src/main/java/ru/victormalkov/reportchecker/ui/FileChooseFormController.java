package ru.victormalkov.reportchecker.ui;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import ru.victormalkov.reportchecker.service.AuthUtil;
import ru.victormalkov.reportchecker.service.DriveFile;
import ru.victormalkov.reportchecker.service.PageCopyUtil;
import ru.victormalkov.reportchecker.service.Updater;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FileChooseFormController {
    @FXML
    ListView<DriveFile> fileListView;

    private final ObservableList<DriveFile> observableList = FXCollections.observableArrayList();
    private final Updater updater = new Updater();

    private void loadFileList() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int attempts = 0;
                while (++attempts <= AuthUtil.MAX_RETRIES) {
                    Drive driveService = null;
                    try {
                        driveService = AuthUtil.getDriveService();
                    } catch (IOException e) {
                        if (attempts < AuthUtil.MAX_RETRIES) {
                            AuthUtil.cleanCredentials();
                            continue;
                        }
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Подключение к Google Drive");
                            alert.setHeaderText("Ошибочка вышла!");
                            alert.setContentText(e.getLocalizedMessage());
                            alert.showAndWait();
                            System.exit(1);
                        });
                    }
                    FileList result = null;
                    try {
                        result = driveService.files().list()
                                .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                                .setOrderBy("modifiedTime desc").execute();
                    } catch (IOException e) {
                        if (attempts < AuthUtil.MAX_RETRIES) {
                            AuthUtil.cleanCredentials();
                            continue;
                        }
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Загрузка списка файлов");
                            alert.setHeaderText("Ошибочка вышла!");
                            alert.setContentText(e.getLocalizedMessage());
                            alert.showAndWait();
                            System.exit(2);
                        });
                    }

                    List<File> files = result.getFiles();
                    if (files == null || files.isEmpty()) {
                        System.err.println("No files found.");
                    } else {
                        for (File file : files) {
                            observableList.add(new DriveFile(file.getName(), file.getId()));
                        }
                    }
                    return null;
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        fileListView.setItems(observableList);
        if (updater.hasUpdate()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Текущая версия: " + Updater.getVersionString() + ", есть новая " + updater.getRemoteVersion() + ", обновить?",
                    ButtonType.YES,
                    ButtonType.NO
                    );
            alert.setTitle("Отчёты трона");
            alert.setHeaderText("Обновление");
            ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setText("Да");
            ((Button) alert.getDialogPane().lookupButton(ButtonType.NO)).setText("Нет");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                new Thread(updater::doUpdate).start();
                Alert updatingAlert = new Alert(Alert.AlertType.INFORMATION,
                        "Скачиваю новую версию",
                        ButtonType.OK);
                updatingAlert.setTitle("Отчёты трона");
                updatingAlert.setHeaderText("Обновление");
                ((Button) updatingAlert.getDialogPane().lookupButton(ButtonType.OK)).setText("Жду");
                updatingAlert.showAndWait();
                return;
            }
        }
        loadFileList();
    }

    @FXML
    private void click(ActionEvent e) throws IOException {
        if (fileListView.getSelectionModel().getSelectedItems().size() == 1) {
            String selectedName = fileListView.getSelectionModel().getSelectedItems().get(0).getName();
            if (selectedName.startsWith("ТРОН")) {
                ReportCheckerUI.sellReportSpreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
                final String otherName = selectedName.replace("ТРОН", "ОТЧЁТ");
                observableList.stream().filter(file -> file.getName().equals(otherName)).findAny()
                        .ifPresent(file -> ReportCheckerUI.dailyReportSpreadsheetId = file.getId());
                if (ReportCheckerUI.dailyReportSpreadsheetId == null) {
                    final String otherName2 = selectedName.replace("ТРОН", "ОТЧЕТ");
                    observableList.stream().filter(file -> file.getName().equals(otherName2)).findAny()
                            .ifPresent(file -> ReportCheckerUI.dailyReportSpreadsheetId = file.getId());
                }
            } else if (selectedName.startsWith("ОТЧЁТ") || selectedName.startsWith("ОТЧЕТ")) {
                ReportCheckerUI.dailyReportSpreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
                final String otherName = selectedName.replace("ОТЧЁТ", "ТРОН").replace("ОТЧЕТ", "ТРОН");
                observableList.stream().filter(file -> file.getName().equals(otherName)).findAny()
                        .ifPresent(file -> ReportCheckerUI.sellReportSpreadsheetId = file.getId());
            } else {
                ReportCheckerUI.sellReportSpreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
                ReportCheckerUI.dailyReportSpreadsheetId = null;
            }

            ReportCheckerUI.instance.changeScene("/forms/ResultForm.fxml");
        }
    }

    @FXML
    public void copy1page(ActionEvent e) {
        if (fileListView.getSelectionModel().getSelectedItems().size() == 1) {
            String spreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
            try {
                int created = PageCopyUtil.copy1Page(spreadsheetId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Создание страниц");
                alert.setHeaderText(null);
                alert.setContentText("Создано страниц: " + created);
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Создание страниц");
                alert.setHeaderText("Ошибочка вышла!");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }
}
