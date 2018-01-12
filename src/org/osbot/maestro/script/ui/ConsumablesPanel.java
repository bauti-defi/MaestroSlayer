package org.osbot.maestro.script.ui;

import org.osbot.maestro.script.data.Foods;
import org.osbot.maestro.script.data.Potions;
import org.osbot.maestro.script.data.SlayerSettings;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ConsumablesPanel extends JPanel implements ActionListener, ChangeListener {

    private final SlayerSettings settings;
    private final JRadioButton antipoison, antidote;
    private final ButtonGroup antiChoice;
    private final JComboBox<Potions> potions;
    private final JComboBox<Foods> foods;
    private final JSpinner amount, baseEat, maxEat;
    private final JList<Potion> potionsToUse;
    private final DefaultListModel<Potion> potionsToUseModel;
    private final JButton addPotion, deletePotion;


    public ConsumablesPanel(SlayerSettings settings) {
        this.settings = settings;

        Box food = Box.createVerticalBox();
        this.foods = new JComboBox<>(Foods.values());
        this.foods.setSelectedItem(Foods.getByName(settings.getFood().getName()));
        this.foods.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateFood();
            }
        });
        food.add(foods);

        food.add(new JLabel("Amount"));
        this.amount = new JSpinner();
        this.amount.setValue(settings.getFood().getAmount());
        this.amount.addChangeListener(this::stateChanged);
        food.add(amount);

        food.add(new JLabel("Eat randomly between (HP %)"));
        this.baseEat = new JSpinner();
        this.baseEat.setValue(settings.getFood().getMinPercentToEatAt());
        this.baseEat.addChangeListener(this::stateChanged);
        food.add(baseEat);

        food.add(new JLabel("and"));
        this.maxEat = new JSpinner();
        this.maxEat.setValue(settings.getFood().getMaxPercentToEatAt());
        this.maxEat.addChangeListener(this::stateChanged);
        food.add(maxEat);
        add(food, BorderLayout.WEST);

        Box potionControl = Box.createHorizontalBox();
        this.potions = new JComboBox<>(Potions.values());
        potionControl.add(potions);

        this.addPotion = new JButton("Add potion");
        this.addPotion.setActionCommand("add");
        this.addPotion.addActionListener(this::actionPerformed);
        potionControl.add(addPotion);
        add(potionControl, BorderLayout.NORTH);

        Box potion = Box.createVerticalBox();
        this.potionsToUseModel = new DefaultListModel<>();
        this.potionsToUse = new JList<>(potionsToUseModel);
        potion.add(potionsToUse);

        this.deletePotion = new JButton("Delete potion");
        this.deletePotion.setActionCommand("delete");
        this.deletePotion.addActionListener(this::actionPerformed);
        potion.add(deletePotion);
        add(potion);

        Box antiBox = Box.createHorizontalBox();
        this.antiChoice = new ButtonGroup();
        this.antidote = new JRadioButton("Antidote");
        antiBox.add(antidote);
        this.antipoison = new JRadioButton("Antipoison");
        antiBox.add(antipoison);
        this.antiChoice.add(antidote);
        this.antiChoice.add(antipoison);
        this.antidote.setSelected(settings.isUseAntidote());
        add(antiBox, BorderLayout.SOUTH);

    }

    private void updateFood() {
        settings.setFood(new Food((Foods) foods.getSelectedItem(), (int) amount.getValue(), (int) baseEat.getValue(), (int)
                maxEat.getValue()));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "delete":
                if (!potionsToUse.isSelectionEmpty()) {
                    //delete potion from settings list
                    potionsToUse.remove(potionsToUse.getSelectedIndex());
                    potionsToUse.clearSelection();
                }
                break;
            case "add":
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateFood();
    }
}
