package com.quui.tm2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class FileIO {

    public static String read(String url) {
        try {
            AmasLogger.singleton(FileIO.class).info(
                    "Current directory: " + new File(".").getAbsolutePath());
            Scanner s = new Scanner(new URL(url).openStream());
            String all = "";
            while (s.hasNextLine()) {
                String nextLine = s.nextLine();
                all += nextLine.replaceAll("	", "  ") + "\n";
            }
            return all;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(String html, String location) {
        FileWriter writer;
        try {
            writer = new FileWriter(new File(new URL(location).toURI()));
            writer.write(html);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        AmasLogger.singleton(FileIO.class).info(
                String.format("Wrote %s chars to %s", html.length(), location));
    }

}
