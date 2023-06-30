package com.mshevchenko.gui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class AddRemoveProduct extends JDialog {

    public static final int ADD = 1;
    public static final int REMOVE = 2;

    private JButton okButton;
    private JButton cancelButton;
    private JLabel quantityLabel;
    private JFormattedTextField quantityTextField;
    private MainFrame mainFrame;

    private int[] ids;
    private int type;

    public AddRemoveProduct(Frame owner, int[] ids, int type) {
        super(owner, true);
        this.mainFrame = (MainFrame) owner;
        this.ids = ids;
        this.type = type;
        init();
    }

    private void init() {
        initButtons();
        initLabels();
        initTextFields();
        initListeners();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(MainFrame.BACKGROUND_COLOR_FIRST);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        buttonPanel.add(this.okButton);
        buttonPanel.add(this.cancelButton);

        JPanel auxiliaryPanel = new JPanel();
        auxiliaryPanel.setBackground(MainFrame.BACKGROUND_COLOR_FIRST);
        auxiliaryPanel.setLayout(new BoxLayout(auxiliaryPanel, BoxLayout.Y_AXIS));
        auxiliaryPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        auxiliaryPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        auxiliaryPanel.add(this.quantityLabel);
        auxiliaryPanel.add(this.quantityTextField);
        auxiliaryPanel.add(buttonPanel);

        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(auxiliaryPanel);
        this.getContentPane().setBackground(MainFrame.BACKGROUND_COLOR_FIRST);
        if(this.type == ADD) {
            this.setTitle("Add");
        }
        else {
            this.setTitle("Remove");
        }
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(this.mainFrame);
    }

    private void initButtons() {
        this.okButton = new JButton("Ok");
        this.okButton.setBackground(MainFrame.BACKGROUND_BUTTON_COLOR);
        this.okButton.setForeground(MainFrame.FOREGROUND_BUTTON_COLOR);

        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setBackground(MainFrame.BACKGROUND_BUTTON_COLOR);
        this.cancelButton.setForeground(MainFrame.FOREGROUND_BUTTON_COLOR);
    }

    private void initLabels() {
        this.quantityLabel = new JLabel("Quantity");
        this.quantityLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.quantityLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    private void initTextFields() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        this.quantityTextField = new JFormattedTextField(formatter);
    }

    private void initListeners() {
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = quantityTextField.getValue() == null ? 0 : (int) quantityTextField.getValue();
                try {
                    dispose();
                    if (type == ADD) {
                        if(!mainFrame.getClient().increaseProductsQuantity(ids, quantity)) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Products quantity was not increased!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        if(!mainFrame.getClient().decreaseProductQuantity(ids[0], quantity)) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Product quantity was not decreased!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    mainFrame.update();
                }
                catch (Exception ex) {
                    dispose();
                    mainFrame.processException(ex);
                }
            }
        });
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

}
