package ru.victormalkov.reportchecker.ui;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public void initialize() throws IOException {
        Drive driveService = AuthUtil.getDriveService();
        FileList result = driveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                .setOrderBy("modifiedTime desc").execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.err.println("No files found.");
        } else {
            for (File file : files) {
                observableList.add(new DriveFile(file.getName(), file.getId()));
            }
        }

        fileListView.setItems(observableList);
    }

    private void click(ActionEvent e) {

    }
}
