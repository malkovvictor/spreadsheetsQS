package ru.victormalkov;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportChecker {
    private static final String APP_NAME = "TRON Report Checker";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String regexprCache = "(\\d+)(?![ТTЮтю0-9])";
    private static final String regexprOnline = "(\\d+)(?=[Юю])";
    private static final String regexprTerminal = "(\\d+)(?=[ТTт])";
    private static final Pattern pcache = Pattern.compile(regexprCache);
    private static final Pattern ponline = Pattern.compile(regexprOnline);
    private static final Pattern pterminal = Pattern.compile(regexprTerminal);


    private static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        InputStream in = ReportChecker.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (null == in) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        //final String spreadsheetId = "1-VoVp6VvzeAQZgmMv4Bx0yGq9Q0GrBk-sK6AUtgE7NM";
        Sheets service = new Sheets.Builder(transport, JSON_FACTORY, getCredentials(transport))
                .setApplicationName(APP_NAME)
                .build();

        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId)
                .setIncludeGridData(true)
                .execute();

        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet sheet : sheets) {
            System.out.println("Sheet: " + sheet.getProperties().getTitle());
            GridData data = sheet.getData().get(0);
            if (data == null) {
                System.out.println("No data found");
            } else {
                List<RowData> rdata = data.getRowData();
                if (rdata == null || rdata.size() < 2) {
                    System.out.println("Too few rows");
                } else {
                    Integer border1 = null;
                    Integer border2 = null;
                    for (int i = 2; i < rdata.size(); i++) {
                        RowData row = rdata.get(i);
                        List<CellData> rowdata = row.getValues();
                        if (rowdata == null || rowdata.size() < 2) {
                            // nothing
                        } else if (border1 == null) {
                            CellData cdata = rowdata.get(1);
                            if ((cdata.getFormattedValue() == null
                                    || cdata.getFormattedValue().isBlank()
                                    || isGreen(cdata)
                                    || isBlue(cdata))
                                    && border1 == null) {
                                border1 = i;
                            }
                        } else {
                            CellData cdata = rowdata.get(0);
                            if (isRed(cdata)) {
                                border2 = i;
                                break;
                            }
                        }
                    }
                    System.out.println("Borders: " + border1 + ", " + border2);

                    Day report = new Day();

                    if (border1 == null || border2 == null) {
                        System.out.println("not all borders found -- cannot parse this sheet");
                    } else {
                        doCount(rdata, 2, border1, report);
                        doCount(rdata, border1, border2, report);
                        doCount(rdata, border2 + 1, rdata.size(), report);

                        System.out.println(report);
                    }
                }
            }
        }
    }

    private static void doCount(List<RowData> rdata, int from, int to, Day report) {
        for (int i = from; i < to; i++) {
            List<CellData> rowdata = rdata.get(i).getValues();
            if (rowdata != null) {
                for (CellData cdata : rowdata) {
                    if (isBlue(cdata) || isGreen(cdata)) {
                        String s = cdata.getFormattedValue();
                        Matcher m = pcache.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayCache(Integer.parseInt(m.group()));
                            } else {
                                report.addNightCache(Integer.parseInt(m.group()));
                            }
                        }

                        m = ponline.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayOnline(Integer.parseInt(m.group()));
                            } else {
                                report.addNightOnline(Integer.parseInt(m.group()));
                            }
                        }

                        m = pterminal.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayTerminal(Integer.parseInt(m.group()));
                            } else {
                                report.addNightTerminal(Integer.parseInt(m.group()));
                            }
                        }
                    }
                }
            }
        }

    }

    public static final double eps = 0.02;
    public static boolean isGreen(Color color) {
        return n0(color.getGreen()) > n0(color.getBlue()) + eps &&
                n0(color.getGreen()) > n0(color.getRed()) + eps;
    }

    public static boolean isGreen(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isGreen(data.getEffectiveFormat().getBackgroundColor());
    }

    /* RGB(0, 1, 1) is blue */
    public static boolean isBlue(Color color) {
        return n0(color.getBlue()) >= n0(color.getGreen()) &&
                n0(color.getBlue()) > n0(color.getRed()) + eps;
    }
    public static boolean isBlue(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isBlue(data.getEffectiveFormat().getBackgroundColor());
    }


    public static boolean isRed(Color color) {
        return n0(color.getRed()) > n0(color.getGreen()) + eps &&
                n0(color.getRed()) > n0(color.getBlue()) + eps;
    }
    public static boolean isRed(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isRed(data.getEffectiveFormat().getBackgroundColor());
    }


    public static final Float n0(Float f) {
        return f == null ? 0 : f;
    }
}
