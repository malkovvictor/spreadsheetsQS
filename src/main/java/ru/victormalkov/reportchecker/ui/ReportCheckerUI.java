package ru.victormalkov.reportchecker.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportCheckerUI extends Application {
    public static ReportCheckerUI instance;
    public static String spreadsheetId; //dirty hack

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        ReportCheckerUI.instance = this;
        Parent fileChooseForm = FXMLLoader.load(getClass().getResource("/forms/FileChooseForm.fxml"));
        Scene scene = new Scene(fileChooseForm);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        primaryStage.getScene().setRoot(pane);
    }
}
