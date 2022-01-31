package ru.victormalkov.reportchecker.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.victormalkov.reportchecker.ui.Starter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Updater {
    public static final String UPDATE_URL = "https://api.github.com/repos/malkovvictor/spreadsheetsQS/releases/latest";

    private String downloadURL;

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
        String jsonStr = null;
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
            String myVersionStr = Starter.class.getPackage().getImplementationVersion();
            System.out.println("my version: " + myVersionStr);
            ModuleDescriptor.Version myVersion = ModuleDescriptor.Version.parse(myVersionStr);

            return myVersion.compareTo(remoteVersion) < 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void doUpdate() {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(UPDATE_URL).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("aaaa.jar");
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
