package ru.victormalkov.reportchecker.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class Updater {
    public static final String UPDATE_URL = "https://api.github.com/repos/malkovvictor/spreadsheetsQS/releases/latest";

    private String downloadURL;
    private String newJarName;

    public boolean hasUpdate() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(UPDATE_URL);
        request.addHeader("content-type", "application/json");
        HttpResponse result = null;
        try {
            result = httpClient.execute(request);
        } catch (IOException e) {
            return false;
        }
        String jsonStr;
        try {
            jsonStr = EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (IOException e) {
            return false;
        }

        try {
            JsonElement element = JsonParser.parseString(jsonStr);
            JsonObject jsonObject = element.getAsJsonObject();
            String tagName = jsonObject.get("tag_name").getAsString();
            ModuleDescriptor.Version remoteVersion = ModuleDescriptor.Version.parse(tagName);
            element = jsonObject.get("assets");
            jsonObject = element.getAsJsonArray().get(0).getAsJsonObject();
            element = jsonObject.get("browser_download_url");
            downloadURL = element.getAsString();
            String myVersionStr = getVersionString();
            System.out.println("my version: " + myVersionStr);
            if (myVersionStr == null)
                return false;
            ModuleDescriptor.Version myVersion = ModuleDescriptor.Version.parse(myVersionStr);

            return myVersion.compareTo(remoteVersion) < 0;
        } catch (Exception e) {
            // Update is optional, so every exception means we do not need to update now
            e.printStackTrace();
            return false;
        }
    }

    public void doUpdate() {
        try {
            String jarPath = getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("/") + 1);
            newJarName = jarPath + downloadURL.substring(downloadURL.lastIndexOf("/") + 1);
            System.out.println("path: " + jarPath);
            System.out.println("new jar name: " + newJarName);
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(downloadURL).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(newJarName);
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            startNewJarAndCloseCurrent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startNewJarAndCloseCurrent() throws IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File newJar = new File(newJarName);

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(newJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        Platform.exit();
        System.exit(0);
    }

    public static String getVersionString() {
        return Updater.class.getPackage().getImplementationVersion();
    }

}
