package org.osbot.maestro.script.ui;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.data.SlayerSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainFrame extends JFrame implements ActionListener {

    private final SlayerSettings settings;
    private final JTabbedPane tabbedPane;
    private final JPanel slayerPanel, consumablesPanel;
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenuItem rebuildCache, save, debug;
    private final JButton start;

    public MainFrame(SlayerSettings settings) {
        super("MaestroSlayer");
        this.settings = settings;

        this.menuBar = new JMenuBar();
        this.fileMenu = new JMenu("File");

        this.save = new JMenuItem("Save");
        this.save.setActionCommand("save");
        this.save.addActionListener(this::actionPerformed);

        this.rebuildCache = new JMenuItem("Rebuild Cache");
        this.rebuildCache.setActionCommand("rebuild");
        this.rebuildCache.addActionListener(this::actionPerformed);

        this.debug = new JCheckBoxMenuItem("Debug");
        this.debug.setSelected(true);
        this.debug.setEnabled(false);
        this.debug.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setDebug(debug.isSelected());
            }
        });

        this.fileMenu.add(save);
        this.fileMenu.add(rebuildCache);
        this.fileMenu.add(debug);
        this.menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        this.tabbedPane = new JTabbedPane();
        this.slayerPanel = new SlayerPanel(settings);
        this.tabbedPane.addTab("Slayer", slayerPanel);
        this.consumablesPanel = new ConsumablesPanel(settings);
        this.tabbedPane.addTab("Potions & Food", consumablesPanel);
        add(tabbedPane, BorderLayout.CENTER);

        this.start = new JButton("Start");
        this.start.setActionCommand("start");
        this.start.addActionListener(this::actionPerformed);
        add(start, BorderLayout.SOUTH);

        pack();
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
            case "start":
                dispose();
                break;
        }
    }
}
