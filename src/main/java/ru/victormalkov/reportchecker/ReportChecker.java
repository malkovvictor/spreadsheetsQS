package ru.victormalkov.reportchecker;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import ru.victormalkov.reportchecker.service.AuthUtil;
import ru.victormalkov.reportchecker.service.DailyReport;
import ru.victormalkov.reportchecker.service.DailySellReportReader;
import ru.victormalkov.reportchecker.service.Day;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class ReportChecker {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";

        //final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";


        Drive driveService = AuthUtil.getDriveService();
        FileList result = driveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                .setOrderBy("modifiedTime desc").execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }


        DailySellReportReader sellReportReader = new DailySellReportReader();
        DailyReport report = sellReportReader.readReport(spreadsheetId);
        for (Day d : report.getDays()) {
            System.out.print(d.toPrettyString());
        }

        System.in.read();
    }

}
