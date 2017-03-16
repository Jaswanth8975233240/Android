package com.intelliq.appengine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private static final String KEYSTORE_PROPERTIES_FILE_NAME = "keystore.properties";

    public static final String MESSAGE_BIRD_KEY_DEV = "MESSAGE_BIRD_KEY_DEV";
    public static final String MESSAGE_BIRD_KEY_PRODUCTION = "MESSAGE_BIRD_KEY_PRODUCTION";

    private static KeyStore instance;
    private Properties keys = new Properties();

    private KeyStore() {
        try {
            InputStream inputStream = new FileInputStream(getPropertiesFile());
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

    /**
     * Attempts to find the {@link #KEYSTORE_PROPERTIES_FILE_NAME} and returns it as File.
     *
     * @return
     */
    private static File getPropertiesFile() throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        files.add(new File("WEB-INF/" + KEYSTORE_PROPERTIES_FILE_NAME));
        files.add(new File(System.getProperty("user.dir") + "/backend/src/main/webapp/WEB-INF/" + KEYSTORE_PROPERTIES_FILE_NAME));

        for (File file : files) {
            if (file.exists()) {
                return file;
            } else {
                log.info("Unable to find keystore properties file: " + file.getAbsolutePath());
            }
        }

        File propertiesFile = findPropertiesFile(Paths.get(System.getProperty("user.dir")));
        if (propertiesFile.exists()) {
            log.info("Found keystore properties file: " + propertiesFile.getAbsolutePath());
            return propertiesFile;
        } else {
            throw new FileNotFoundException("Unable to find any keystore properties file");
        }
    }

    /**
     * Traverses the specified directory and returns the first file that
     * matches the {@link #KEYSTORE_PROPERTIES_FILE_NAME}.
     *
     * @param dir
     * @return
     */
    private static File findPropertiesFile(Path dir) throws FileNotFoundException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    return findPropertiesFile(path);
                } else {
                    if (path.getFileName().toString().contains(KEYSTORE_PROPERTIES_FILE_NAME)) {
                        return path.toFile();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileNotFoundException("Unable to find file with name: " + KEYSTORE_PROPERTIES_FILE_NAME);
    }

}
