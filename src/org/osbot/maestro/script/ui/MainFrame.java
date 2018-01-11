package org.osbot.maestro.script.ui;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.data.SlayerSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainFrame extends JFrame implements ActionListener {

    private final SlayerSettings settings;
    private final JTabbedPane tabbedPane;
    private final SlayerPanel slayerPanel;
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenuItem rebuildCache, save;

    public MainFrame(SlayerSettings settings) {
        super("MaestroSlayer");
        this.settings = settings;
        if (settings != null) {
            applySettings();
        }

        this.menuBar = new JMenuBar();
        this.fileMenu = new JMenu("File");

        this.save = new JMenuItem("Save");
        this.save.setActionCommand("save");
        this.save.addActionListener(this::actionPerformed);

        this.rebuildCache = new JMenuItem("Rebuild Cache");
        this.rebuildCache.setActionCommand("rebuild");
        this.rebuildCache.addActionListener(this::actionPerformed);

        this.fileMenu.add(save);
        this.fileMenu.add(rebuildCache);
        this.menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        this.tabbedPane = new JTabbedPane();
        this.slayerPanel = new SlayerPanel(settings);
        this.tabbedPane.addTab("Slayer", slayerPanel);

        setContentPane(tabbedPane);
        pack();
    }

    private void applySettings() {
        //set values
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "rebuild":
                RuntimeVariables.cache.rebuild();
                break;
            case "save":
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(RuntimeVariables.cache.getPath());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(settings);
                    objectOutputStream.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;

        }
    }
}
