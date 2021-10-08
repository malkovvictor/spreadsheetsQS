package ru.victormalkov;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class QuickStart {
    private static String APP_NAME = "Quickstart for Google Spreadsheets API";
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        InputStream in = QuickStart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (null == in) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8899).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1OPVrXiPLh3LtM8rIveEPhUJQjgEtyM08T7i5bvYE3Gw";
        final String range = "01,09,2021!A2:B50";
        Sheets service = new Sheets.Builder(transport, JSON_FACTORY, getCredentials(transport))
                .setApplicationName(APP_NAME)
                .build();

        Spreadsheet responce = service.spreadsheets().get(spreadsheetId)
                .setRanges(Collections.singletonList(range))
                .setIncludeGridData(true)
                .execute();

        System.out.println(responce.toPrettyString());


/*        ValueRange responce = service.spreadsheets().get(spreadsheetId)
                .setRanges(Collections.singletonList(range))
                .setIncludeGridData(true)
                .execute();*/

 /*       List<List<Object>> values = responce.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data");
        } else {
            System.out.println("Something found");
            for (List row : values) {
                System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
        } */
    }
}
