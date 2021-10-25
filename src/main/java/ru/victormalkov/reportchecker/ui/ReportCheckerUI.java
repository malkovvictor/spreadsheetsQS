package ru.victormalkov.reportchecker.ui;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.victormalkov.reportchecker.service.AuthUtil;
import ru.victormalkov.reportchecker.service.DriveFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReportCheckerUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent fileChooseForm = FXMLLoader.load(getClass().getResource("/FileChooseForm.fxml"));
        Scene scene = new Scene(fileChooseForm);

        ObservableList<DriveFile> observableList = FXCollections.observableArrayList();



        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
