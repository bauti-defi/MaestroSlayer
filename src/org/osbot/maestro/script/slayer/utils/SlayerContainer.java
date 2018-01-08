package org.osbot.maestro.script.slayer.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.templates.SlayerMasterTemplate;
import org.osbot.maestro.script.slayer.utils.templates.SlayerTaskTemplate;

import java.util.ArrayList;

public class SlayerContainer {

    private ArrayList<SlayerMaster> masters;
    private ArrayList<SlayerTask> tasks;

    public SlayerContainer() {
        this.masters = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public static SlayerContainer wrap(JSONObject jsonObject) {
        SlayerContainer container = new SlayerContainer();
        JSONArray jsonArray = (JSONArray) jsonObject.get(Config.MASTERS);
        for (Object element : jsonArray) {
            container.addMaster(SlayerMasterTemplate.wrap((JSONObject) element));
        }
        jsonArray = (JSONArray) jsonObject.get(Config.TASKS);
        for (Object element : jsonArray) {
            container.addTask(SlayerTaskTemplate.wrap((JSONObject) element));
        }

        return container;
    }

    public ArrayList<SlayerMaster> getMasters() {
        return masters;
    }

    public ArrayList<SlayerTask> getTasks() {
        return tasks;
    }

    private void addMaster(SlayerMasterTemplate template) {
        this.masters.add(SlayerMaster.wrap(template));
    }

    private void addTask(SlayerTaskTemplate template) {
        this.tasks.add(SlayerTask.wrap(template));
    }

}
