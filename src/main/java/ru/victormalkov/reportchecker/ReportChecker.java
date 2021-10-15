package ru.victormalkov.reportchecker;

import ru.victormalkov.reportchecker.service.DailyReport;
import ru.victormalkov.reportchecker.service.DailySellReportReader;
import ru.victormalkov.reportchecker.service.Day;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ReportChecker {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        //final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";

        final String spreadsheetId = "1qjqjJt3Adb9SaoiNtPxjvichhUJd4G2tPnzpurGNgGg";

        DailySellReportReader sellReportReader = new DailySellReportReader();
        DailyReport report = sellReportReader.readReport(spreadsheetId);
        for (Day d : report.getDays()) {
            System.out.print(d.toPrettyString());
        }

        System.in.read();
    }

}
