package com.intelliq.appengine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Steppschuh on 03/03/2017.
 */

public final class KeyStore {

    private static final String KEYSTORE_PROPERTIES_FILE = "WEB-INF/keystore.properties";

    public static final String MESSAGE_BIRD_KEY_DEV = "MESSAGE_BIRD_KEY_DEV";
    public static final String MESSAGE_BIRD_KEY_PRODUCTION = "MESSAGE_BIRD_KEY_PRODUCTION";

    private static KeyStore instance;
    private Properties keys = new Properties();

    private KeyStore() {
        try {
            InputStream inputStream = new FileInputStream(new File(KEYSTORE_PROPERTIES_FILE));
            keys.load(inputStream);
        } catch (IOException e) {
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

}
