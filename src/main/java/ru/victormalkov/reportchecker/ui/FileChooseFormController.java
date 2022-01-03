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
import javafx.scene.control.ListView;
import ru.victormalkov.reportchecker.service.AuthUtil;
import ru.victormalkov.reportchecker.service.DriveFile;
import ru.victormalkov.reportchecker.service.PageCopyUtil;

import java.io.IOException;
import java.util.List;

public class FileChooseFormController {
    @FXML
    ListView<DriveFile> fileListView;

    private final ObservableList<DriveFile> observableList = FXCollections.observableArrayList();

    private void loadFileList() {
        Task<Void> task = new Task<Void>() {
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

    public void initialize() {
        Platform.runLater(() -> fileListView.setItems(observableList));
        loadFileList();
    }

    @FXML
    private void click(ActionEvent e) throws IOException {
        if (fileListView.getSelectionModel().getSelectedItems().size() == 1) {
            ReportCheckerUI.spreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
            ReportCheckerUI.instance.changeScene("/forms/ResultForm.fxml");
        }
    }

    @FXML
    public void copy1page(ActionEvent e) throws IOException {
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
