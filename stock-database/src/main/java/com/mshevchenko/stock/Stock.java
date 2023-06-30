package com.mshevchenko.stock;



import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Product;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Stock {

    private Connection connection;
    private String filename;

    public Stock() throws SQLException, ClassNotFoundException {
        this("stock-database/src/main/resources/stock.db");
    }

    public Stock(String filename) throws SQLException, ClassNotFoundException {
        this.filename = filename;
        createDirectories(this.filename);
        init();
    }

    private void createDirectories(String filename) {
        File file = new File(filename);
        String path = file.getPath();
        String name = file.getName();
        File directories = new File(path.substring(0, path.length() - name.length()));
        directories.mkdirs();
    }

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.filename);
        setForeignKeysOn();
        createGroupsTable();
        createProductsTable();
    }

    private void setForeignKeysOn() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys = ON");
            statement.close();
        } catch (SQLException e) {
            System.out.println("Can't set foreign keys on!");
            //e.printStackTrace();
        }
    }

    private void createGroupsTable() {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS groups (\n" +
                        "group_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "group_name TEXT NOT NULL UNIQUE,\n" +
                        "group_description TEXT\n" +
                        ");\n");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            //System.out.println("groups table was not created!");
            //e.printStackTrace();
        }
    }

    private void createProductsTable() {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS products (\n" +
                            "product_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "group_id INTEGER NOT NULL,\n" +
                            "product_name TEXT NOT NULL UNIQUE,\n" +
                            "product_description TEXT,\n" +
                            "producer TEXT,\n" +
                            "price DOUBLE DEFAULT 0,\n" +
                            "quantity INTEGER DEFAULT 0,\n" +
                            "FOREIGN KEY (group_id)\n" +
                            "REFERENCES groups (group_id)\n" +
                            "ON DELETE CASCADE\n" +
                            "ON UPDATE NO ACTION\n" +
                            ");\n");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            //System.out.println("products table was not created!");
            //e.printStackTrace();
        }
    }

    public int insertGroup(Group group) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO groups (group_name, group_description)\n" +
                            "VALUES(?, ?);\n");
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not inserted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int insertProduct(Product product) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO products (group_id, product_name, product_description, producer, price, quantity)\n" +
                        "VALUES(?, ?, ?, ?, ?, ?);\n");
            statement.setInt(1, product.getGroupId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setString(4, product.getProducer());
            statement.setDouble(5, product.getPrice());
            statement.setInt(6, product.getQuantity());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not inserted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int deleteGroup(Group group) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "DELETE FROM groups\n" +
                            "WHERE group_id = ?;\n");
            statement.setInt(1, group.getGroupId());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not deleted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int deleteProduct(Product product) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "DELETE FROM products\n" +
                            "WHERE product_id = ?;\n");
            statement.setInt(1, product.getProductId());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not deleted!");
        }
        return result;
    }

    public int updateGroup(Group group) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "UPDATE groups\n" +
                            "SET group_name = ?,\n" +
                            "group_description = ?\n" +
                            "WHERE group_id = ?;\n");
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.setInt(3, group.getGroupId());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not updated!");
            //e.printStackTrace();
        }
        return result;
    }

    public int updateProduct(Product product) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "UPDATE products\n" +
                            "SET group_id = ?,\n" +
                            "product_name = ?,\n" +
                            "product_description = ?,\n" +
                            "producer = ?,\n" +
                            "price = ?,\n" +
                            "quantity = ?\n" +
                            "WHERE product_id = ?;\n");
            statement.setInt(1, product.getGroupId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setString(4, product.getProducer());
            statement.setDouble(5, product.getPrice());
            statement.setInt(6, product.getQuantity());
            statement.setInt(7, product.getProductId());
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not updated!");
            //e.printStackTrace();
        }
        return result;
    }

    public List<Group> getGroups() {
        List<Group> groups = new LinkedList<>();
        try {
            Statement statement  = this.connection.createStatement();
            ResultSet res = statement.executeQuery(
                    "SELECT group_id, group_name, group_description\n" +
                        "FROM groups;\n");
            while(res.next()) {
                int groupId = res.getInt("group_id");
                String name = res.getString("group_name");
                String description = res.getString("group_description");
                groups.add(new Group(groupId, name, description));
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get groups!");
            //e.printStackTrace();
        }
        return groups;
    }

    public List<Product> getProducts() {
        List<Product> products = new LinkedList<>();
        try {
            Statement statement  = this.connection.createStatement();
            ResultSet res = statement.executeQuery(
                        "SELECT product_id, group_id, product_name, product_description, producer, price, quantity\n" +
                            "FROM products;\n");
            while(res.next()) {
                int productId = res.getInt("product_id");
                int groupId = res.getInt("group_id");
                String name = res.getString("product_name");
                String description = res.getString("product_description");
                String producer = res.getString("producer");
                double price = res.getDouble("price");
                int quantity = res.getInt("quantity");
                products.add(new Product(productId, groupId, name, description, producer, price, quantity));
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get products!");
            //e.printStackTrace();
        }
        return products;
    }

    public List<Group> getGroupsByFilter(Group group) {
        List<Group> groups = new LinkedList<>();
        try {
            PreparedStatement statement  = this.connection.prepareStatement(
                    "SELECT group_id, group_name, group_description\n" +
                        "FROM groups\n" +
                        "WHERE group_id LIKE ?\n" +
                        "AND group_name LIKE ?\n" +
                        "AND group_description LIKE ?;\n");
            if(group.getGroupId() > 0) {
                statement.setInt(1, group.getGroupId());
            }
            else {
                statement.setString(1, "%");
            }
            if(group.getName() != null && !group.getName().isEmpty()) {
                statement.setString(2, group.getName());
            }
            else {
                statement.setString(2, "%");
            }
            if(group.getDescription() != null && !group.getDescription().isEmpty()) {
                statement.setString(3, group.getDescription());
            }
            else {
                statement.setString(3, "%");
            }
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                int groupId = res.getInt("group_id");
                String name = res.getString("group_name");
                String description = res.getString("group_description");
                groups.add(new Group(groupId, name, description));
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get groups by filter!");
            //e.printStackTrace();
        }
        return groups;
    }

    public List<Product> getProductsByFilter(Product product) {
        List<Product> products = new LinkedList<>();
        try {
            PreparedStatement statement  = this.connection.prepareStatement(
                    "SELECT product_id, group_id, product_name, product_description, producer, price, quantity\n" +
                        "FROM products\n" +
                        "WHERE product_id LIKE ?\n" +
                        "AND group_id LIKE ?\n" +
                        "AND product_name LIKE ?\n" +
                        "AND product_description LIKE ?\n" +
                        "AND producer LIKE ?\n" +
                        "AND price LIKE ?\n" +
                        "AND quantity LIKE ?;\n");
            if(product.getProductId() > 0) {
                statement.setInt(1, product.getProductId());
            }
            else {
                statement.setString(1, "%");
            }
            if(product.getGroupId() > 0) {
                statement.setInt(2, product.getGroupId());
            }
            else {
                statement.setString(2, "%");
            }
            if(product.getName() != null && !product.getName().isEmpty()) {
                statement.setString(3, product.getName());
            }
            else {
                statement.setString(3, "%");
            }
            if(product.getDescription() != null && !product.getDescription().isEmpty()) {
                statement.setString(4, product.getDescription());
            }
            else {
                statement.setString(4, "%");
            }
            if(product.getProducer() != null && !product.getProducer().isEmpty()) {
                statement.setString(5, product.getProducer());
            }
            else {
                statement.setString(5, "%");
            }
            if(product.getPrice() > 0) {
                statement.setDouble(6, product.getPrice());
            }
            else {
                statement.setString(6, "%");
            }
            if(product.getQuantity() > 0) {
                statement.setInt(7, product.getQuantity());
            }
            else {
                statement.setString(7, "%");
            }
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                int productId = res.getInt("product_id");
                int groupId = res.getInt("group_id");
                String name = res.getString("product_name");
                String description = res.getString("product_description");
                String producer = res.getString("producer");
                double price = res.getDouble("price");
                int quantity = res.getInt("quantity");
                products.add(new Product(productId, groupId, name, description, producer, price, quantity));
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get products by filter!");
            //e.printStackTrace();
        }
        return products;
    }

    public Group getGroupById(int id) {
        Group group = null;
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT group_id, group_name, group_description\n" +
                        "FROM groups\n" +
                        "WHERE group_id = ?;\n");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                int groupId = res.getInt("group_id");
                String name = res.getString("group_name");
                String description = res.getString("group_description");
                group = new Group(groupId, name, description);
                break;
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get groups!");
            //e.printStackTrace();
        }
        return group;
    }

    public Product getProductById(int id) {
        Product product = null;
        try {
            PreparedStatement statement  = this.connection.prepareStatement(
                    "SELECT product_id, group_id, product_name, product_description, producer, quantity, price\n" +
                        "FROM products\n" +
                        "WHERE product_id = ?;\n");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                int productId = res.getInt("product_id");
                int groupId = res.getInt("group_id");
                String name = res.getString("product_name");
                String description = res.getString("product_description");
                String producer = res.getString("producer");
                double price = res.getDouble("price");
                int quantity = res.getInt("quantity");
                product = new Product(productId, groupId, name, description, producer, price, quantity);
                break;
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get products!");
            //e.printStackTrace();
        }
        return product;
    }

    public int deleteGroupById(int id) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM groups\n" +
                            "WHERE group_id = ?;\n");
            statement.setInt(1, id);
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not deleted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int deleteGroupsByIds(int[] ids) {
        int result = 0;
        try{
            if(ids.length == 0) {
                return result;
            }
            StringBuilder param = new StringBuilder();
            param.append('?');
            for(int i = 1; i < ids.length; i++) {
                param.append(", ").append('?');
            }
            PreparedStatement statement = this.connection.prepareStatement(
                        "DELETE FROM groups\n" +
                            "WHERE group_id IN (" + param + ");\n");
            for(int i = 0; i < ids.length; i++) {
                statement.setInt(i+1, ids[i]);
            }
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not deleted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int deleteProductById(int id) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM products\n" +
                            "WHERE product_id = ?;\n");
            statement.setInt(1, id);
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not deleted!");
            //e.printStackTrace();
        }
        return result;
    }

    public int deleteProductsByIds(int[] ids) {
        int result = 0;
        try{
            if(ids.length == 0) {
                return result;
            }
            StringBuilder param = new StringBuilder();
            param.append('?');
            for(int i = 1; i < ids.length; i++) {
                param.append(", ").append('?');
            }
            PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM products\n" +
                            "WHERE product_id IN (" + param + ");\n");
            for(int i = 0; i < ids.length; i++) {
                statement.setInt(i+1, ids[i]);
            }
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("group was not deleted!");
            //e.printStackTrace();
        }
        return result;
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    public List<String[]> getProductsInnerJoinGroups() {
        List<String[]> products = new LinkedList<>();
        try {
            Statement statement  = this.connection.createStatement();
            ResultSet res = statement.executeQuery(
                        "SELECT product_id, group_name, product_name, product_description, producer, price, quantity\n" +
                            "FROM products\n" +
                            "INNER JOIN groups ON products.group_id = groups.group_id;\n");
            while(res.next()) {
                String productId = res.getString("product_id");
                String groupName = res.getString("group_name");
                String name = res.getString("product_name");
                String description = res.getString("product_description");
                String producer = res.getString("producer");
                String price = res.getString("price");
                String quantity = res.getString("quantity");
                products.add(new String[] {productId, groupName, name, description, producer, price, quantity});
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get products!");
            //e.printStackTrace();
        }
        return products;
    }

    public List<String[]> getProductsInnerJoinGroupsByFilter(Product product) {
        List<String[]> products = new LinkedList<>();
        try {
            PreparedStatement statement  = this.connection.prepareStatement(
                    "SELECT product_id, group_name, product_name, product_description, producer, price, quantity\n" +
                            "FROM products\n" +
                            "INNER JOIN groups ON products.group_id = groups.group_id\n" +
                            "WHERE product_id LIKE ?\n" +
                            "AND products.group_id LIKE ?\n" +
                            "AND product_name LIKE ?\n" +
                            "AND product_description LIKE ?\n" +
                            "AND producer LIKE ?\n" +
                            "AND price LIKE ?\n" +
                            "AND quantity LIKE ?;\n");
            if(product.getProductId() > 0) {
                statement.setInt(1, product.getProductId());
            }
            else {
                statement.setString(1, "%");
            }
            if(product.getGroupId() > 0) {
                statement.setInt(2, product.getGroupId());
            }
            else {
                statement.setString(2, "%");
            }
            if(product.getName() != null && !product.getName().isEmpty()) {
                statement.setString(3, product.getName());
            }
            else {
                statement.setString(3, "%");
            }
            if(product.getDescription() != null && !product.getDescription().isEmpty()) {
                statement.setString(4, product.getDescription());
            }
            else {
                statement.setString(4, "%");
            }
            if(product.getProducer() != null && !product.getProducer().isEmpty()) {
                statement.setString(5, product.getProducer());
            }
            else {
                statement.setString(5, "%");
            }
            if(product.getPrice() > 0) {
                statement.setDouble(6, product.getPrice());
            }
            else {
                statement.setString(6, "%");
            }
            if(product.getQuantity() > 0) {
                statement.setInt(7, product.getQuantity());
            }
            else {
                statement.setString(7, "%");
            }
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                String productId = res.getString("product_id");
                String groupName = res.getString("group_name");
                String name = res.getString("product_name");
                String description = res.getString("product_description");
                String producer = res.getString("producer");
                String price = res.getString("price");
                String quantity = res.getString("quantity");
                products.add(new String[] {productId, groupName, name, description, producer, price, quantity});
            }
            statement.close();
        } catch (SQLException e) {
            //System.out.println("Can't get products by filter!");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
        return products;
    }

    public int increaseProductQuantity(int id, int value) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE products\n" +
                            "SET quantity = quantity + ?\n" +
                            "WHERE product_id = ?;\n");
            statement.setInt(1, value);
            statement.setInt(2, id);
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not updated!");
            //e.printStackTrace();
        }
        return result;
    }

    public int increaseProductsQuantity(int[] ids, int value) {
        int result = 0;
        try{
            if(ids.length == 0) {
                return result;
            }
            StringBuilder param = new StringBuilder();
            param.append('?');
            for(int i = 1; i < ids.length; i++) {
                param.append(", ").append('?');
            }
            PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE products\n" +
                            "SET quantity = quantity + ?\n" +
                            "WHERE product_id IN (" + param + ");\n");
            statement.setInt(1, value);
            for(int i = 0; i < ids.length; i++) {
                statement.setInt(i + 2, ids[i]);
            }
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not updated!");
            //e.printStackTrace();
        }
        return result;
    }

    public int decreaseProductQuantity(int id, int value) {
        int result = 0;
        try{
            PreparedStatement statement = this.connection.prepareStatement(
                        "UPDATE products\n" +
                            "SET quantity = quantity - ?\n" +
                            "WHERE product_id = ? AND quantity >= ?;\n");
            statement.setInt(1, value);
            statement.setInt(2, id);
            statement.setInt(3, value);
            result = statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            //System.out.println("product was not updated!");
            //e.printStackTrace();
        }
        return result;
    }

}
