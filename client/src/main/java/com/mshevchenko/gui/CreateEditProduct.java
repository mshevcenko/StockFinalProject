package com.mshevchenko.gui;

import com.mshevchenko.client.exceptions.InvalidQueryException;
import com.mshevchenko.client.exceptions.ServerErrorException;
import com.mshevchenko.client.exceptions.UnavailableServerException;
import com.mshevchenko.gui.exceptions.NoGroupsException;
import com.mshevchenko.gui.exceptions.NoSuchProductException;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class CreateEditProduct extends JDialog {

    private JButton okButton;
    private JButton cancelButton;

    private JLabel groupLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JLabel producerLabel;
    private JLabel priceLabel;

    private JTextField nameTextField;
    private JTextField descriptionTextField;
    private JTextField producerTextField;
    private JFormattedTextField priceTextField;

    private JComboBox groupComboBox;

    private List<Group> groups;

    private MainFrame mainFrame;

    private int productId = -1;
    private Product product;

    public CreateEditProduct(Frame owner) throws NoGroupsException, UnavailableServerException, ServerErrorException {
        super(owner, true);
        this.mainFrame = (MainFrame) owner;
        init();
    }

    public CreateEditProduct(Frame owner, int productId) throws NoGroupsException, UnavailableServerException, ServerErrorException, InvalidQueryException, NoSuchProductException {
        super(owner, true);
        this.mainFrame = (MainFrame) owner;
        this.productId = productId;
        this.product = this.mainFrame.getClient().getProductById(this.productId);
        if(this.product == null) {
            throw new NoSuchProductException();
        }
        init();
    }

    private void init() throws NoGroupsException, UnavailableServerException, ServerErrorException {
        initButtons();
        initLabels();
        initTextFields();
        initComboBox();
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
        auxiliaryPanel.add(this.groupLabel);
        auxiliaryPanel.add(this.groupComboBox);
        auxiliaryPanel.add(this.nameLabel);
        auxiliaryPanel.add(this.nameTextField);
        auxiliaryPanel.add(this.descriptionLabel);
        auxiliaryPanel.add(this.descriptionTextField);
        auxiliaryPanel.add(this.producerLabel);
        auxiliaryPanel.add(this.producerTextField);
        auxiliaryPanel.add(this.priceLabel);
        auxiliaryPanel.add(this.priceTextField);
        auxiliaryPanel.add(buttonPanel);

        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(auxiliaryPanel);
        this.getContentPane().setBackground(MainFrame.BACKGROUND_COLOR_FIRST);
        if(this.product == null) {
            this.setTitle("Create product");
        }
        else {
            this.setTitle("Edit product");
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
        this.groupLabel = new JLabel("Group");
        this.groupLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.groupLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.nameLabel = new JLabel("Name");
        this.nameLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.nameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.descriptionLabel = new JLabel("Description");
        this.descriptionLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.descriptionLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.producerLabel = new JLabel("Producer");
        this.producerLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.producerLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.priceLabel = new JLabel("Price");
        this.priceLabel.setForeground(MainFrame.FOREGROUND_LABEL_COLOR);
        this.priceLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    private void initTextFields() {
        this.nameTextField = new JTextField();
        this.nameTextField.setPreferredSize(new Dimension(150, 32));
        this.nameTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.product != null) {
            this.nameTextField.setText(this.product.getName());
        }

        this.descriptionTextField = new JTextField();
        this.descriptionTextField.setPreferredSize(new Dimension(150, 32));
        this.descriptionTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.product != null) {
            this.descriptionTextField.setText(this.product.getDescription());
        }

        this.producerTextField = new JTextField();
        this.producerTextField.setPreferredSize(new Dimension(150, 32));
        this.producerTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.product != null) {
            this.producerTextField.setText(this.product.getProducer());
        }

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
        this.priceTextField = new JFormattedTextField(decimalFormat);
        this.priceTextField.setPreferredSize(new Dimension(150, 32));
        this.priceTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if(this.product != null) {
            this.priceTextField.setValue(this.product.getPrice());
        }
    }

    private void initComboBox() throws NoGroupsException, UnavailableServerException, ServerErrorException {
        this.groupComboBox = new JComboBox();
        this.groups = this.mainFrame.getClient().getGroups();
        if(this.groups.size() == 0) {
            throw new NoGroupsException();
        }
        for(Group group : this.groups) {
            this.groupComboBox.addItem(group.getName());
            if(this.product != null && this.product.getGroupId() == group.getGroupId()) {
                this.groupComboBox.setSelectedItem(group.getName());
            }
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
                    priceTextField.setValue(priceTextField.getFormatter().stringToValue(priceTextField.getText()));
                } catch (ParseException ex) {
                    priceTextField.setValue(priceTextField.getValue());
                }
                int groupId = groups.get(groupComboBox.getSelectedIndex()).getGroupId();
                String name = nameTextField.getText();
                String description = descriptionTextField.getText();
                String producer = producerTextField.getText();
                double price = priceTextField.getText() == null || priceTextField.getText().isEmpty() ? 0 : Double.parseDouble(priceTextField.getText());
                if(price < 0) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Price cannot be negative!",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                try {
                    dispose();
                    if (product == null) {
                        Product product = new Product(0, groupId, name, description, producer, price, 0);
                        if(!mainFrame.getClient().insertProduct(product)) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Product was not inserted!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        product.setGroupId(groupId);
                        product.setName(name);
                        product.setDescription(description);
                        product.setProducer(producer);
                        product.setPrice(price);
                        //System.out.println(product);
                        //System.out.println(mainFrame.getClient().updateProduct(product));
                        if(!mainFrame.getClient().updateProduct(product)) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Product was not updated!",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    mainFrame.update();
                }
                catch (Exception ex) {
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
