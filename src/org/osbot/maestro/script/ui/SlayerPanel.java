package org.osbot.maestro.script.ui;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.data.SlayerSettings;
import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SlayerPanel extends JPanel implements ActionListener {

    private final JComboBox<SlayerMaster> masters;
    private final SlayerSettings settings;
    private final JList<SlayerTask> supportedTasks, skippedTasks;
    private final DefaultListModel<SlayerTask> supportedTasksModel, skippedTasksModel;
    private final JButton skipTask, unSkipTask;
    private final JCheckBox cannon;

    public SlayerPanel(SlayerSettings settings) {
        this.settings = settings;

        this.masters = new JComboBox<>();
        for (int index = 0; index < RuntimeVariables.slayerContainer.getMasters().size(); index++) {
            SlayerMaster master = RuntimeVariables.slayerContainer.getMasters().get(index);
            this.masters.addItem(master);
            if (settings.getSlayerMaster() != null) {
                if (master.getName().equalsIgnoreCase(settings.getSlayerMaster())) {
                    this.masters.setSelectedIndex(index);
                }
            }
        }
        this.masters.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SlayerMaster selectedMaster = (SlayerMaster) e.getItem();
                settings.setSlayerMaster(selectedMaster.getName());
            }
        });
        add(masters, BorderLayout.NORTH);

        this.skippedTasksModel = new DefaultListModel<>();
        this.skippedTasks = new JList<>(skippedTasksModel);
        this.skippedTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(skippedTasks, BorderLayout.EAST);

        this.supportedTasksModel = new DefaultListModel<>();
        for (SlayerTask task : RuntimeVariables.slayerContainer.getTasks()) {
            if (!settings.getTasksToSkip().contains(task.getName())) {
                supportedTasksModel.addElement(task);
                continue;
            }
            skippedTasksModel.addElement(task);
        }
        this.supportedTasks = new JList<>(supportedTasksModel);
        this.supportedTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(supportedTasks, BorderLayout.WEST);


        Box skipControls = Box.createVerticalBox();
        this.skipTask = new JButton(">>");
        this.skipTask.setActionCommand("skip");
        this.skipTask.addActionListener(this::actionPerformed);
        skipControls.add(skipTask);

        this.unSkipTask = new JButton("<<");
        this.unSkipTask.setActionCommand("unskip");
        this.unSkipTask.addActionListener(this::actionPerformed);
        skipControls.add(unSkipTask);
        add(skipControls, BorderLayout.CENTER);

        this.cannon = new JCheckBox("Use cannon when possible");
        this.cannon.setActionCommand("cannon");
        this.cannon.addActionListener(this::actionPerformed);
        add(cannon, BorderLayout.SOUTH);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "skip":
                if (!supportedTasks.isSelectionEmpty()) {
                    SlayerTask task = supportedTasks.getSelectedValue();
                    settings.addTaskToSkip(task.getName());
                    skippedTasksModel.addElement(task);
                    supportedTasksModel.removeElement(task);
                    supportedTasks.clearSelection();
                }
                break;
            case "unskip":
                if (!skippedTasks.isSelectionEmpty()) {
                    SlayerTask task = skippedTasks.getSelectedValue();
                    settings.removeTaskToSkip(task.getName());
                    supportedTasksModel.addElement(task);
                    skippedTasksModel.removeElement(task);
                    skippedTasks.clearSelection();
                }
                break;
            case "cannon":
                settings.setUseCannon(cannon.isSelected());
                break;
        }
    }
}
