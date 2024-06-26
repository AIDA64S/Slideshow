package org.teacon.slides.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.http.HttpHost;
import org.teacon.slides.Slideshow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public final class Config {
    private static boolean proxySwitch = false;
    private static String host = "localhost";
    private static int port = 8080;
    private static int viewDistance = 256;
    private static HttpHost PROXY;

    private static final String PROXY_SWITCH = "proxySwitch";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String VIEW_DISTANCE = "slideshowViewDistance";
    public static final int MAX_VIEW_DISTANCE = 512;

    private static final Path CONFIG_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("slideshow.json");

    public static boolean isProxySwitch() {
        return proxySwitch;
    }

    public static int getViewDistance() {
        return viewDistance;
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static HttpHost getPROXY() {
        return PROXY;
    }

    public static void setProxySwitch(boolean proxySwitch) {
        Config.proxySwitch = proxySwitch;
    }

    public static void setHost(String host) {
        Config.host = host;
    }

    public static void setPort(int port) {
        Config.port = port;
    }

    public static void setViewDistance(int viewDistance) {
        Config.viewDistance = viewDistance;
    }

    public static void saveToFile() {
        writeToFile();
    }

    public static void refreshProperties() {
        Slideshow.LOGGER.info("Refreshed Slideshow mod config");
        try {
            final JsonObject jsonConfig = JsonParser.parseString(String.join("", Files.readAllLines(CONFIG_PATH))).getAsJsonObject();
            try {
                proxySwitch = jsonConfig.get(PROXY_SWITCH).getAsBoolean();
            } catch (Exception ignored) {
            }
            try {
                host = jsonConfig.get(HOST).getAsString();
            } catch (Exception ignored) {
            }
            try {
                port = jsonConfig.get(PORT).getAsInt();
            } catch (Exception ignored) {
            }
            try {
                viewDistance = jsonConfig.get(VIEW_DISTANCE).getAsInt();
            } catch (Exception ignored) {
            }
            if (proxySwitch) {
                PROXY = new HttpHost(host, port);
                Slideshow.LOGGER.info("Proxy loaded");
                Slideshow.LOGGER.info(HOST + ": {}", host);
                Slideshow.LOGGER.info(PORT + ": {}", port);
            } else {
                PROXY = null;
            }
        } catch (Exception e) {
            writeToFile();
            refreshProperties();
        }
    }

    private static void writeToFile() {
        Slideshow.LOGGER.info("Wrote Slideshow mod config to file");
        final JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty(PROXY_SWITCH, proxySwitch);
        jsonConfig.addProperty(HOST, host);
        jsonConfig.addProperty(PORT, port);
        jsonConfig.addProperty(VIEW_DISTANCE, viewDistance);
        try {
            if (!Files.exists(CONFIG_PATH.getParent())) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            Files.write(CONFIG_PATH, Collections.singleton(prettyPrint(jsonConfig)));
        } catch (IOException e) {
            Slideshow.LOGGER.error("Configuration file write exception", e);
        }
    }

    private static String prettyPrint(JsonElement jsonElement) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement);
    }
}