package org.osbot.maestro.script.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.maestro.script.slayer.utils.SlayerContainer;
import org.osbot.maestro.script.util.directory.Directory;
import org.osbot.maestro.script.util.directory.exceptions.InvalidFileNameException;

import java.io.*;

public class Cache {

    private final Directory directory;

    public Cache(String path) {
        this.directory = new Directory(path);
        if (!validate()) {
            rebuild();
        } else {
            loadSlayerDataLocal();
        }
    }

    private Directory getDirectory() {
        return directory;
    }

    public String getPath() {
        return directory.getPath();
    }

    public SlayerSettings getSettings() {
        try {
            if (directory.getFile(Config.SLAYER_SETTINGS_FILE_NAME).exists()) {
                FileInputStream inputStream = new FileInputStream(directory.getFile(Config.SLAYER_SETTINGS_FILE_NAME));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                return (SlayerSettings) objectInputStream.readObject();
            }
        } catch (InvalidFileNameException e) {
            e.printStackTrace();
            return new SlayerSettings();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new SlayerSettings();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new SlayerSettings();
    }

    public void rebuild() {
        try {
            if (directory.delete()) {
                if (directory.create()) {
                    //log("Script directory created at: " + RuntimeVariables.saveDirectory.getPath());
                    downloadSlayerData();
                    loadSlayerDataLocal();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validate() {
        return directory.exists();
    }

    private void downloadSlayerData() {
        // log("Downloading latest version...");
        //logic
        //log("Download finished.");
    }

    private void loadSlayerDataLocal() {
        // log("Loading slayer data...");
        JSONParser parser = new JSONParser();
        JSONObject slayerData = null;
        try (FileReader reader = new FileReader(directory.getFile(Config.SLAYER_DATA_FILE_NAME))) {
            slayerData = (JSONObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFileNameException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (slayerData == null) {
            //  warn("ERROR LOADING LOCAL SLAYER DATA!");
        }
        RuntimeVariables.slayerContainer = SlayerContainer.wrap(slayerData);
        //log("Slayer data loaded.");
    }
}
