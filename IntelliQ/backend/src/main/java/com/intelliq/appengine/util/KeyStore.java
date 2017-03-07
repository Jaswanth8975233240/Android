package com.intelliq.appengine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Steppschuh on 03/03/2017.
 */

public final class KeyStore {

    private static final Logger log = Logger.getLogger(KeyStore.class.getName());

    private static final File KEYSTORE_PROPERTIES_FILE = getPropertiesFile();

    public static final String MESSAGE_BIRD_KEY_DEV = "MESSAGE_BIRD_KEY_DEV";
    public static final String MESSAGE_BIRD_KEY_PRODUCTION = "MESSAGE_BIRD_KEY_PRODUCTION";

    private static KeyStore instance;
    private Properties keys = new Properties();

    private KeyStore() {
        try {
            InputStream inputStream = new FileInputStream(KEYSTORE_PROPERTIES_FILE);
            keys.load(inputStream);
        } catch (IOException e) {
            log.severe("Unable to load keystore properties file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static KeyStore getInstance() {
        if (instance == null) {
            instance = new KeyStore();
        }
        return instance;
    }

    public static String getKey(String key) {
        return getInstance().keys.getProperty(key, key);
    }

    public Properties getKeys() {
        return keys;
    }

    private static File getPropertiesFile() {
        String fileName = "keystore.properties";
        String productionPath = "WEB-INF/" + fileName;
        String testingPath = System.getProperty("user.dir") + "/backend/src/main/webapp/WEB-INF/" + fileName;

        List<File> files = new ArrayList<>();
        files.add(new File(productionPath));
        files.add(new File(testingPath));

        for (File file : files) {
            if (file.exists()) {
                return file;
            } else {
                log.info("Unable to find keystore properties file: " + file.getAbsolutePath());
            }
        }

        log.warning("Unable to find any keystore properties file.");

        List<String> fileNames = getFileNames(new ArrayList<String>(), Paths.get(System.getProperty("user.dir")));
        for (String name : fileNames) {
            log.info(name);
        }

        return new File("");
    }

    private static List<String> getFileNames(List<String> fileNames, Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    if (path.getFileName().toString().contains("keystore")) {
                        fileNames.add(path.toAbsolutePath().toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

}
