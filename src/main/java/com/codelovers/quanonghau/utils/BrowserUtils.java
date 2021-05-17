package com.codelovers.quanonghau.utils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class BrowserUtils {
    public static void browse(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Runtime runtime = Runtime.getRuntime();
                String os = System.getProperty("os.name");
                if (os.equals("Mac OS X")) {
                    String[] args = { "osascript", "-e", "open location \"" + url + "\"" };
                    Process process = runtime.exec(args);
                } else {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
