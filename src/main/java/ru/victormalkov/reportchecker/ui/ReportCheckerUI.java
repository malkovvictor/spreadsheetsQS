package ru.victormalkov.reportchecker.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class ReportCheckerUI extends Application {
    public static ReportCheckerUI instance;
    public static String sellReportSpreadsheetId;
    public static String dailyReportSpreadsheetId;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        JMetro jMetro = new JMetro(Style.LIGHT);
        this.primaryStage = primaryStage;
        ReportCheckerUI.instance = this;
        Parent fileChooseForm = FXMLLoader.load(getClass().getResource("/forms/FileChooseForm.fxml"));
        Scene scene = new Scene(fileChooseForm);
        jMetro.setScene(scene);

        String stylesheet = getClass().getResource("/pibug.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        stylesheet = getClass().getResource("/reports.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/tron.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        primaryStage.getScene().setRoot(pane);
    }
}
