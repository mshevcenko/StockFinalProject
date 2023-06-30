package com.mshevchenko.gui;

import com.mshevchenko.client.exceptions.InvalidQueryException;
import com.mshevchenko.client.exceptions.ServerErrorException;
import com.mshevchenko.client.exceptions.UnavailableServerException;
import com.mshevchenko.gui.exceptions.NoSuchGroupException;
import com.mshevchenko.stock_objects.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateEditGroup extends JDialog {

    private JButton okButton;
    private JButton cancelButton;

    private JLabel nameLabel;
    private JLabel descriptionLabel;

    private JTextField nameTextField;
    private JTextField descriptionTextField;

    private MainFrame mainFrame;

    private int groupId = -1;
    private Group group;

    public CreateEditGroup(Frame owner) {
        super(owner, true);
        this.mainFrame = (MainFrame) owner;
        init();
    }

    public CreateEditGroup(Frame owner, int groupId) throws UnavailableServerException, ServerErrorException, InvalidQueryException, NoSuchGroupException {
        super(owner, true);
        this.mainFrame = (MainFrame) owner;
        this.groupId = groupId;
        this.group = this.mainFrame.getClient().getGroupById(this.groupId);
        if(this.group == null) {
            throw new NoSuchGroupException();
        }
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
        auxiliaryPanel.add(this.nameLabel);
        auxiliaryPanel.add(this.nameTextField);
        auxiliaryPanel.add(this.descriptionLabel);
        auxiliaryPanel.add(this.descriptionTextField);
        auxiliaryPanel.add(buttonPanel);

        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(auxiliaryPanel);
        this.getContentPane().setBackground(MainFrame.BACKGROUND_COLOR_FIRST);
        if(this.groupId > 0) {
            this.setTitle("Edit group");
        }
        else {
            this.setTitle("Create group");
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
        this.nameLabel = new JLabel("Name");
        this.nameLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.nameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.descriptionLabel = new JLabel("Description");
        this.descriptionLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.descriptionLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    private void initTextFields() {
        this.nameTextField = new JTextField();
        this.nameTextField.setPreferredSize(new Dimension(150, 32));
        this.nameTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.group != null) {
            this.nameTextField.setText(this.group.getName());
        }

        this.descriptionTextField = new JTextField();
        this.descriptionTextField.setPreferredSize(new Dimension(150, 32));
        this.descriptionTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.group != null) {
            this.descriptionTextField.setText(this.group.getDescription());
        }
    }

    private void initListeners() {
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nameTextField.getText() == null || nameTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Name cannot be empty!",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                try {
                    dispose();
                    if (group == null) {
                        if(!mainFrame.getClient().insertGroup(new Group(0, nameTextField.getText(), descriptionTextField.getText()))) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Group was not inserted!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        group.setName(nameTextField.getText());
                        group.setDescription(descriptionTextField.getText());
                        if (!mainFrame.getClient().updateGroup(group)) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Group was not updated!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    mainFrame.update();
                }
                catch(Exception ex) {
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
