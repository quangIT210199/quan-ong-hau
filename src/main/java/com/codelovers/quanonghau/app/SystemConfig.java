package com.codelovers.quanonghau.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SystemConfig {
    private static String CONFIG_ROOT = "conf/";
    private static Properties properties = null;

    public static void loadAllProperties() throws IOException, IllegalArgumentException{
        File dir = new File(CONFIG_ROOT);
        String[] children = dir.list();

        if (children == null) {
            // Either dir does not exist or is not a  directory
            return;
        } else {
            properties = new Properties();
            for (int i = 0; i < children.length; i++) { // Get filename of
                // file or directory
                String filename = CONFIG_ROOT + children[i];
                Properties prop = loadProperties(filename);
                if (prop != null){
                    properties.putAll(prop);
                }
            }
        }
    }

    public static int storeProperty(String key, String value, String section){
        try {
            String filename = CONFIG_ROOT + section + ".properties";
            Properties prop = loadProperties(filename);
            if (prop == null){
                return 3;
            }
            prop.setProperty(key, value);
            prop.store(new FileOutputStream(filename), null);
            properties.setProperty(key, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        }

        return 0;
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    private static Properties loadProperties(String filename) throws IOException, IllegalArgumentException{
        if (!filename.endsWith(".properties")){
            return null;
        }
        Properties properties = new Properties();
        FileInputStream file = null;
        file = new FileInputStream(filename);
        properties.load(file);
        file.close();
        return properties;
    }
}
