package ru.victormalkov.reportchecker.service;

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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.logging.log4j.LogManager;
import ru.victormalkov.reportchecker.ui.Starter;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;

public class AuthUtil {
    public static final String APP_NAME = "TRON Report Checker";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static NetHttpTransport transport = null;

    static {
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            LogManager.getLogger(AuthUtil.APP_NAME).fatal(e);
            System.exit(0);
        }
    }


    public static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        InputStream in = Starter.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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

    public static Sheets getSheetsService() throws IOException {
        return new Sheets.Builder(transport, AuthUtil.JSON_FACTORY, AuthUtil.getCredentials(transport))
                .setApplicationName(AuthUtil.APP_NAME)
                .build();
    }

    public static Drive getDriveService() throws IOException {
        return new Drive.Builder(transport, JSON_FACTORY, getCredentials(transport))
                .setApplicationName(AuthUtil.APP_NAME)
                .build();
    }
}
