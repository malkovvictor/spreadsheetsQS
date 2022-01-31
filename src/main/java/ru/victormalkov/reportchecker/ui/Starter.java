package ru.victormalkov.reportchecker.ui;

import ru.victormalkov.reportchecker.service.Updater;

public class Starter {
    public static void main(String[] args) {
        Updater up = new Updater();
        if (up.hasUpdate()) {
            up.doUpdate();
        } else {
            ReportCheckerUI.launch(ReportCheckerUI.class, args);
        }
    }
}
