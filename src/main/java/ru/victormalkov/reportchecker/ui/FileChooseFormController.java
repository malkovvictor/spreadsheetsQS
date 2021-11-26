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
import javafx.scene.control.ListView;
import ru.victormalkov.reportchecker.service.AuthUtil;
import ru.victormalkov.reportchecker.service.DriveFile;

import java.io.IOException;
import java.util.List;

public class FileChooseFormController {
    @FXML
    private ListView<DriveFile> fileListView;

    private ObservableList<DriveFile> observableList = FXCollections.observableArrayList();

    private void loadFileList() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Drive driveService = null;
                try {
                    driveService = AuthUtil.getDriveService();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                FileList result = null;
                try {
                    result = driveService.files().list()
                            .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                            .setOrderBy("modifiedTime desc").execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(2);
                }
                List<File> files = result.getFiles();
                if (files == null || files.isEmpty()) {
                    System.err.println("No files found.");
                } else {
                    for (File file : files) {
                        observableList.add(new DriveFile(file.getName(), file.getId()));
                    }
                }

                fileListView.setItems(observableList);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void initialize() {
        loadFileList();
    }

    @FXML
    private void click(ActionEvent e) throws IOException {
        if (fileListView.getSelectionModel().getSelectedItems().size() == 1) {
            ReportCheckerUI.spreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
            ReportCheckerUI.instance.changeScene("/forms/ResultForm.fxml");
        }
    }

    private void copy1page(ActionEvent e) throws IOException {
        if (fileListView.getSelectionModel().getSelectedItems().size() == 1) {
            String spreadsheetId = fileListView.getSelectionModel().getSelectedItems().get(0).getId();
        }
    }
}
