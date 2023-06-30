package com.mshevchenko.gui;

import com.mshevchenko.client.StockClient;
import com.mshevchenko.client.exceptions.InvalidQueryException;
import com.mshevchenko.client.exceptions.ServerErrorException;
import com.mshevchenko.client.exceptions.UnavailableServerException;
import com.mshevchenko.gui.exceptions.NoGroupsException;
import com.mshevchenko.gui.exceptions.NoSuchGroupException;
import com.mshevchenko.gui.exceptions.NoSuchProductException;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {

    public static final int DEFAULT_WIDTH = 900;
    public static final int DEFAULT_HEIGHT = 700;
    public static final Color BACKGROUND_BUTTON_COLOR = new Color(0x214F4B);
    public static final Color FOREGROUND_BUTTON_COLOR = Color.WHITE;
    public static final Color FOREGROUND_LABEL_COLOR = Color.WHITE;
    public static final Color BACKGROUND_COLOR_FIRST = new Color(0x16C172);
    public static final Color BACKGROUND_COLOR_SECOND = new Color(0x72FFB6);
    public static final String[] GROUP_HEADERS = new String[] {"Id", "Name", "Description"};
    public static final String[] PRODUCT_HEADERS = new String[] {"Id", "Group", "Name", "Description", "Producer", "Price", "Quantity"};

    private StockClient client;

    private JPanel controlPanel;
    private JPanel filterPanel;
    private JPanel infoPanel;
    private JScrollPane tableScrollPane;
    private JPanel auxiliaryPanel;

    private DisplayedTable displayedTable;
    private DefaultTableModel tableModel;
    private JTable table;

    private JButton groupsButton;
    private JButton productsButton;
    private JButton createButton;
    private JButton editButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton searchButton;

    private JLabel nameLabel;
    private JLabel groupLabel;
    private JLabel summaryCostLabel;

    private JTextField nameTextField;
    private JTextField summaryCostTextField;

    private JComboBox groupComboBox;

    private List<Group> groups;

    public MainFrame(StockClient client) {
        this.client = client;
        init();
    }

    private void init() {
        initTable();
        initButtons();
        initLabels();
        initTextFields();
        initComboBox();
        initListeners();
        initPanels();
        this.getContentPane().setBackground(BACKGROUND_COLOR_SECOND);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(this.controlPanel, BorderLayout.LINE_START);
        this.getContentPane().add(this.auxiliaryPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Stock");
        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void initTable() {
        this.tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(this.tableModel);
        this.table.getTableHeader().setReorderingAllowed(false);
    }

    private void initButtons() {
        this.groupsButton = new JButton("Groups");
        this.groupsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.groupsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.groupsButton.getBorder()));
        this.groupsButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.groupsButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.productsButton = new JButton("Products");
        this.productsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.productsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.productsButton.getBorder()));
        this.productsButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.productsButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.createButton = new JButton("Create");
        this.createButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.createButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.createButton.getBorder()));
        this.createButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.createButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.editButton = new JButton("Edit");
        this.editButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.editButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.editButton.getBorder()));
        this.editButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.editButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.addButton = new JButton("Add");
        this.addButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.addButton.getBorder()));
        this.addButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.addButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.removeButton = new JButton("Remove");
        this.removeButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.removeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.removeButton.getBorder()));
        this.removeButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.removeButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.deleteButton = new JButton("Delete");
        this.deleteButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.deleteButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.deleteButton.getBorder()));
        this.deleteButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.deleteButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.updateButton = new JButton("Update");
        this.updateButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.updateButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 5, 0, 5, BACKGROUND_COLOR_FIRST),
                this.updateButton.getBorder()));
        this.updateButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.updateButton.setForeground(FOREGROUND_BUTTON_COLOR);

        this.searchButton = new JButton("Search");
        this.searchButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.searchButton.setBackground(BACKGROUND_BUTTON_COLOR);
        this.searchButton.setForeground(FOREGROUND_BUTTON_COLOR);
    }

    private void initLabels() {
        this.nameLabel = new JLabel("Name:");
        this.nameLabel.setForeground(FOREGROUND_LABEL_COLOR);
        this.groupLabel = new JLabel("Group:");
        this.groupLabel.setForeground(FOREGROUND_LABEL_COLOR);
        this.summaryCostLabel = new JLabel("Summary cost:");
        this.summaryCostLabel.setForeground(FOREGROUND_LABEL_COLOR);
    }

    private void initTextFields() {
        this.nameTextField = new JTextField();
        this.nameTextField.setPreferredSize(new Dimension(200, 32));
        this.summaryCostTextField = new JTextField();
        this.summaryCostTextField.setEditable(false);
        this.summaryCostTextField.setPreferredSize(new Dimension(100, 32));
    }

    private void initComboBox() {
        this.groupComboBox = new JComboBox();
        this.groupComboBox.setPreferredSize(new Dimension(150, 32));
    }

    private void initListeners() {
        this.groupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDisplayedTable(DisplayedTable.GROUPS);
            }
        });
        this.productsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDisplayedTable(DisplayedTable.PRODUCTS);
            }
        });
        this.createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.GROUPS) {
                    createGroup();
                }
                else if(displayedTable == DisplayedTable.PRODUCTS) {
                    createProduct();
                }
            }
        });
        this.editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.GROUPS) {
                    editGroup();
                }
                else if(displayedTable == DisplayedTable.PRODUCTS) {
                    editProduct();
                }
            }
        });
        this.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.PRODUCTS) {
                    addProducts();
                }
            }
        });
        this.removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.PRODUCTS) {
                    removeProduct();
                }
            }
        });
        this.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.GROUPS) {
                    deleteGroups();
                }
                else if(displayedTable == DisplayedTable.PRODUCTS) {
                    deleteProducts();
                }
            }
        });
        this.updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        this.searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(displayedTable == DisplayedTable.GROUPS) {
                    searchGroups();
                }
                else if(displayedTable == DisplayedTable.PRODUCTS) {
                    searchProducts();
                }
            }
        });
    }

    private void initPanels() {
        this.controlPanel = new JPanel();
        this.controlPanel.setBackground(BACKGROUND_COLOR_FIRST);
        this.controlPanel.setLayout(new BoxLayout(this.controlPanel, BoxLayout.Y_AXIS));
        this.controlPanel.add(this.groupsButton);
        this.controlPanel.add(this.productsButton);
        this.controlPanel.add(this.createButton);
        this.controlPanel.add(this.editButton);
        this.controlPanel.add(this.addButton);
        this.controlPanel.add(this.removeButton);
        this.controlPanel.add(this.deleteButton);
        this.controlPanel.add(this.updateButton);

        this.filterPanel = new JPanel();
        this.filterPanel.setBackground(BACKGROUND_COLOR_FIRST);
        this.filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.filterPanel.add(this.nameLabel);
        this.filterPanel.add(this.nameTextField);
        this.filterPanel.add(this.groupLabel);
        this.filterPanel.add(this.groupComboBox);
        this.filterPanel.add(this.searchButton);

        this.infoPanel = new JPanel();
        this.infoPanel.setBackground(BACKGROUND_COLOR_FIRST);
        this.infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.infoPanel.add(this.summaryCostLabel);
        this.infoPanel.add(this.summaryCostTextField);

        this.tableScrollPane = new JScrollPane(this.table);
        this.tableScrollPane.setBackground(BACKGROUND_COLOR_SECOND);

        this.auxiliaryPanel = new JPanel();
        this.auxiliaryPanel.setBackground(BACKGROUND_COLOR_SECOND);
        this.auxiliaryPanel.setLayout(new BorderLayout());
        this.auxiliaryPanel.add(this.filterPanel, BorderLayout.PAGE_START);
        this.auxiliaryPanel.add(this.infoPanel, BorderLayout.PAGE_END);
        this.auxiliaryPanel.add(this.tableScrollPane, BorderLayout.CENTER);
    }

    public void update() {
        try {
            if (this.displayedTable == DisplayedTable.GROUPS) {
                updateGroups();
            } else if (this.displayedTable == DisplayedTable.PRODUCTS) {
                updateProducts();
            }
        }
        catch (Exception e) {
            processException(e);
        }
    }

    private void updateGroups() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        this.tableModel.setRowCount(0);
        this.tableModel.setColumnIdentifiers(GROUP_HEADERS);
        if(this.nameTextField.getText() == null || this.nameTextField.getText().isEmpty()) {
            this.groups = this.client.getGroups();
        }
        else {
            this.groups = this.client.getGroupsByFilter(new Group(-1, this.nameTextField.getText(), null));
        }
        for(Group group : this.groups) {
            Object[] groupRow = new Object[] {group.getGroupId(), group.getName(), group.getDescription()};
            this.tableModel.addRow(groupRow);
        }
    }

    private void updateProducts() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        updateComboBox();
        this.tableModel.setRowCount(0);
        this.tableModel.setColumnIdentifiers(PRODUCT_HEADERS);
        /*List<Product> products = new LinkedList<>();
        if((this.nameTextField.getText() == null || this.nameTextField.getText().isEmpty()) && this.groupComboBox.getSelectedIndex() == 0) {
            products = this.stock.getProducts();
        }
        else {
            int groupId = this.groupComboBox.getSelectedIndex() == 0 ? -1 : this.groups.get(this.groupComboBox.getSelectedIndex()-1).getGroupId();
            String name = this.nameTextField.getText();
            products = this.stock.getProductsByFilter(new Product(-1, groupId, name, null, null, -1, -1));
        }
        double summaryCost = 0;
        for(Product product : products) {
            Object[] groupRow = new Object[] {product.getProductId(), product.getGroupId(), product.getName(), product.getDescription(), product.getProducer(), product.getPrice(), product.getQuantity()};
            this.tableModel.addRow(groupRow);
            summaryCost += product.getPrice() * product.getQuantity();
        }
        this.summaryCostTextField.setText(String.valueOf(summaryCost));*/
        List<String[]> products = new LinkedList<>();
        if((this.nameTextField.getText() == null || this.nameTextField.getText().isEmpty()) && this.groupComboBox.getSelectedIndex() == 0) {
            products = this.client.getProductsInnerJoinGroups();
        }
        else {
            int groupId = this.groupComboBox.getSelectedIndex() == 0 ? -1 : this.groups.get(this.groupComboBox.getSelectedIndex()-1).getGroupId();
            String name = this.nameTextField.getText();
            products = this.client.getProductsInnerJoinGroupsByFilter(new Product(-1, groupId, name, null, null, -1, -1));
        }
        double summaryCost = 0;
        for(String[] product : products) {
            this.tableModel.addRow(product);
            summaryCost += Double.parseDouble(product[5]) * Integer.parseInt(product[6]);
        }
        this.summaryCostTextField.setText(String.valueOf(summaryCost));
    }

    private void updateComboBox() throws UnavailableServerException, ServerErrorException {
        int groupId = -1;
        if(this.groups != null && this.groups.size() > 0 && this.groupComboBox.getSelectedIndex() > 0) {
            groupId = this.groups.get(this.groupComboBox.getSelectedIndex()-1).getGroupId();
        }
        this.groups = this.client.getGroups();
        this.groupComboBox.removeAllItems();
        this.groupComboBox.addItem("");
        for(Group group : this.groups) {
            this.groupComboBox.addItem(group.getName());
            if(group.getGroupId() == groupId) {
                this.groupComboBox.setSelectedItem(group.getName());
            }
        }
    }

    private void createGroup() {
        CreateEditGroup createEditGroup = new CreateEditGroup(this);
        createEditGroup.setVisible(true);
    }

    private void createProduct() {
        try {
            CreateEditProduct createEditProduct = new CreateEditProduct(this);
            createEditProduct.setVisible(true);
        } catch (NoGroupsException e) {
            JOptionPane.showMessageDialog(this.getRootPane(),
                    "There is no single group!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            update();
        }
        catch (Exception e) {
            processException(e);
        }
    }

    private void editGroup() {
        if(this.table.getSelectedRows().length == 1) {
            int id = Integer.parseInt(this.tableModel.getValueAt(this.table.getSelectedRow(), 0).toString());
            try {
                CreateEditGroup createEditGroup = new CreateEditGroup(this, id);
                createEditGroup.setVisible(true);
            }
            catch (NoSuchGroupException e) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "No such group exists!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                update();
            }
            catch (Exception e) {
                processException(e);
            }
        }
    }

    private void editProduct() {
        if(this.table.getSelectedRows().length == 1) {
            int id = Integer.parseInt(this.tableModel.getValueAt(this.table.getSelectedRow(), 0).toString());
            try {
                CreateEditProduct createEditProduct = new CreateEditProduct(this, id);
                createEditProduct.setVisible(true);
            }
            catch (NoGroupsException e) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "There is no single group!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                update();
            }
            catch (NoSuchProductException e) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "No such product exists!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                update();
            }
            catch (Exception e) {
                processException(e);
            }
        }
    }

    private void addProducts() {
        if(this.table.getSelectedRows().length > 0) {
            int[] selectedRows = this.table.getSelectedRows();
            int[] ids = new int[selectedRows.length];
            for(int i = 0; i < ids.length; i++) {
                ids[i] = Integer.parseInt(this.tableModel.getValueAt(selectedRows[i], 0).toString());
            }
            AddRemoveProduct addRemoveProduct = new AddRemoveProduct(this, ids, AddRemoveProduct.ADD);
            addRemoveProduct.setVisible(true);
        }
    }

    private void removeProduct() {
        if(this.table.getSelectedRows().length == 1) {
            int[] selectedRows = this.table.getSelectedRows();
            int[] ids = new int[selectedRows.length];
            for(int i = 0; i < ids.length; i++) {
                ids[i] = Integer.parseInt(this.tableModel.getValueAt(selectedRows[i], 0).toString());
            }
            AddRemoveProduct addRemoveProduct = new AddRemoveProduct(this, ids, AddRemoveProduct.REMOVE);
            addRemoveProduct.setVisible(true);
        }
    }

    private void deleteGroups() {
        int[] selectedRows = this.table.getSelectedRows();
        if(selectedRows.length <= 0) {
            return;
        }
        int value = JOptionPane.showConfirmDialog(
                MainFrame.this,
                "All products associated with selected groups will be deleted too!\n" +
                        "Do you really want to delete them?",
                "Warning",
                JOptionPane.YES_NO_OPTION);
        if(value == JOptionPane.NO_OPTION) {
            return;
        }
        int[] ids = new int[selectedRows.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Integer.parseInt(this.tableModel.getValueAt(selectedRows[i], 0).toString());
        }
        try {
            if (!this.client.deleteGroupsByIds(ids)) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "Groups were not deleted!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            update();
        } catch (Exception e) {
            processException(e);
        }
    }

    private void deleteProducts() {
        int[] selectedRows = this.table.getSelectedRows();
        int[] ids = new int[selectedRows.length];
        for(int i = 0; i < ids.length; i++) {
            ids[i] = Integer.parseInt(this.tableModel.getValueAt(selectedRows[i], 0).toString());
        }
        try {
            if(!this.client.deleteProductsByIds(ids)) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "Products were not deleted!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            update();
        } catch (Exception e) {
            processException(e);
        }
    }

    public void processException(Exception e) {
        if(e instanceof InvalidQueryException) {
            JOptionPane.showMessageDialog(this.getRootPane(),
                    "Unknown error!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        else if(e instanceof ServerErrorException) {
            JOptionPane.showMessageDialog(this.getRootPane(),
                    "Server error!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        else if(e instanceof UnavailableServerException) {
            JOptionPane.showMessageDialog(this.getRootPane(),
                    "Server is unavailable!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchGroups() {
        update();
    }

    private void searchProducts() {
        update();
    }

    public void setDisplayedTable(DisplayedTable displayedTable) {
        if(this.displayedTable == displayedTable) {
            return;
        }
        this.displayedTable = displayedTable;
        this.nameTextField.setText("");
        if(this.displayedTable == DisplayedTable.GROUPS) {
            this.addButton.setVisible(false);
            this.removeButton.setVisible(false);
            this.groupLabel.setVisible(false);
            this.groupComboBox.setVisible(false);
            this.infoPanel.setVisible(false);
        }
        else if(this.displayedTable == DisplayedTable.PRODUCTS) {
            this.addButton.setVisible(true);
            this.removeButton.setVisible(true);
            this.groupLabel.setVisible(true);
            this.groupComboBox.setVisible(true);
            this.infoPanel.setVisible(true);
        }
        update();
    }

    @Override
    public void dispose() {
        this.client.stop();
        this.client.closeSocket();
        super.dispose();
    }

    public StockClient getClient() {
        return this.client;
    }

    public enum DisplayedTable {
        GROUPS,
        PRODUCTS
    }

}
