package com.mshevchenko.server;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshevchenko.packet.Packet;
import com.mshevchenko.packet.Status;
import com.mshevchenko.packet.Commands;
import com.mshevchenko.server_interfaces.Encryptor;
import com.mshevchenko.server_interfaces.Processor;
import com.mshevchenko.stock.Stock;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Pair;
import com.mshevchenko.stock_objects.Product;

import java.util.List;

public class StockProcessor implements Processor {

    public static String SUCCESS_MESSAGE = "success";
    public static String FAILURE_MESSAGE = "failure";
    public static String ERROR_MESSAGE = "error";
    public static String UNKNOWN_OPERATION = "unknown operation";
    private StockConnection connection;
    private ObjectMapper objectMapper;
    private Encryptor encryptor;
    private Stock stock;
    private long packetNumber = 0;

    public StockProcessor(StockConnection connection, Encryptor encryptor, Stock stock) {
        this.connection = connection;
        this.encryptor = encryptor;
        this.objectMapper = new ObjectMapper();
        this.stock = stock;
    }

    @Override
    public void process(Packet packet) {
        if(packet.getStatus() != Status.CLIENT) {
            this.encryptor.encrypt(packet);
            return;
        }
        switch (packet.getCommand()) {
            case Commands.INSERT_GROUP:
                insertGroup(packet);
                break;
            case Commands.INSERT_PRODUCT:
                insertProduct(packet);
                break;
            case Commands.UPDATE_GROUP:
                updateGroup(packet);
                break;
            case Commands.UPDATE_PRODUCT:
                updateProduct(packet);
                break;
            case Commands.DELETE_GROUP:
                deleteGroup(packet);
                break;
            case Commands.DELETE_PRODUCT:
                deleteProduct(packet);
                break;
            case Commands.GET_GROUPS:
                getGroups(packet);
                break;
            case Commands.GET_PRODUCTS:
                getProducts(packet);
                break;
            case Commands.GET_GROUPS_BY_FILTER:
                getGroupsByFilter(packet);
                break;
            case Commands.GET_PRODUCTS_BY_FILTER:
                getProductsByFilter(packet);
                break;
            case Commands.GET_GROUP_BY_ID:
                getGroupById(packet);
                break;
            case Commands.GET_PRODUCT_BY_ID:
                getProductById(packet);
                break;
            case Commands.DELETE_GROUP_BY_ID:
                deleteGroupById(packet);
                break;
            case Commands.DELETE_PRODUCT_BY_ID:
                deleteProductById(packet);
                break;
            case Commands.DELETE_GROUPS_BY_IDS:
                deleteGroupsByIds(packet);
                break;
            case Commands.DELETE_PRODUCTS_BY_IDS:
                deleteProductsByIds(packet);
                break;
            case Commands.GET_PRODUCTS_INNER_JOIN_GROUPS:
                getProductsInnerJoinGroups(packet);
                break;
            case Commands.GET_PRODUCTS_INNER_JOIN_GROUPS_BY_FILTER:
                getProductsInnerJoinGroupsByFilter(packet);
                break;
            case Commands.INCREASE_PRODUCT_QUANTITY:
                increaseProductQuantity(packet);
                break;
            case Commands.INCREASE_PRODUCTS_QUANTITY:
                increaseProductsQuantity(packet);
                break;
            case Commands.DECREASE_PRODUCT_QUANTITY:
                decreaseProductQuantity(packet);
                break;
            case Commands.STOP:
                this.connection.close();
                return;
            default:
                packet.setStatus(Status.UNKNOWN_OPERATION);
                packet.setMessage(UNKNOWN_OPERATION);
                break;
        }
        this.encryptor.encrypt(packet);
    }

    private void insertGroup(Packet packet) {
        try {
            Group group = objectMapper.readValue(packet.getMessage(), Group.class);
            int result = this.stock.insertGroup(group);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void insertProduct(Packet packet) {
        try {
            Product product = objectMapper.readValue(packet.getMessage(), Product.class);
            int result = this.stock.insertProduct(product);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void updateGroup(Packet packet) {
        try {
            Group group = objectMapper.readValue(packet.getMessage(), Group.class);
            int result = this.stock.updateGroup(group);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void updateProduct(Packet packet) {
        try {
            Product product = objectMapper.readValue(packet.getMessage(), Product.class);
            int result = this.stock.updateProduct(product);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteGroup(Packet packet) {
        try {
            Group group = objectMapper.readValue(packet.getMessage(), Group.class);
            int result = this.stock.deleteGroup(group);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteProduct(Packet packet) {
        try {
            Product product = objectMapper.readValue(packet.getMessage(), Product.class);
            int result = this.stock.deleteProduct(product);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getGroups(Packet packet) {
        try {
            List<Group> groups = this.stock.getGroups();
            String json = this.objectMapper.writeValueAsString(groups);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getProducts(Packet packet) {
        try {
            List<Product> products = this.stock.getProducts();
            String json = this.objectMapper.writeValueAsString(products);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getGroupsByFilter(Packet packet) {
        try {
            Group group = this.objectMapper.readValue(packet.getMessage(), Group.class);
            List<Group> groups = this.stock.getGroupsByFilter(group);
            String json = this.objectMapper.writeValueAsString(groups);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getProductsByFilter(Packet packet) {
        try {
            Product product = this.objectMapper.readValue(packet.getMessage(), Product.class);
            List<Product> products = this.stock.getProductsByFilter(product);
            String json = this.objectMapper.writeValueAsString(products);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getGroupById(Packet packet) {
        try {
            int id = this.objectMapper.readValue(packet.getMessage(), int.class);
            Group group = this.stock.getGroupById(id);
            if(group == null) {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
                return;
            }
            String json = this.objectMapper.writeValueAsString(group);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getProductById(Packet packet) {
        try {
            int id = this.objectMapper.readValue(packet.getMessage(), int.class);
            Product product = this.stock.getProductById(id);
            if(product == null) {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
                return;
            }
            String json = this.objectMapper.writeValueAsString(product);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteGroupById(Packet packet) {
        try {
            int id = this.objectMapper.readValue(packet.getMessage(), int.class);
            int result = this.stock.deleteGroupById(id);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteProductById(Packet packet) {
        try {
            int id = this.objectMapper.readValue(packet.getMessage(), int.class);
            int result = this.stock.deleteProductById(id);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteGroupsByIds(Packet packet) {
        try {
            int[] ids = this.objectMapper.readValue(packet.getMessage(), int[].class);
            int result = this.stock.deleteGroupsByIds(ids);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void deleteProductsByIds(Packet packet) {
        try {
            int[] ids = this.objectMapper.readValue(packet.getMessage(), int[].class);
            int result = this.stock.deleteProductsByIds(ids);
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getProductsInnerJoinGroups(Packet packet) {
        try {
            List<String[]> products = this.stock.getProductsInnerJoinGroups();
            String json = this.objectMapper.writeValueAsString(products);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void getProductsInnerJoinGroupsByFilter(Packet packet) {
        try {
            Product product = this.objectMapper.readValue(packet.getMessage(), Product.class);
            List<String[]> products = this.stock.getProductsInnerJoinGroupsByFilter(product);
            String json = this.objectMapper.writeValueAsString(products);
            packet.setStatus(Status.SUCCESS);
            packet.setMessage(json);
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void increaseProductQuantity(Packet packet) {
        try {
            Pair<Integer, Integer> pair = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>(){});
            int result = this.stock.increaseProductQuantity(pair.getFirst(), pair.getSecond());
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void increaseProductsQuantity(Packet packet) {
        try {
            Pair<int[], Integer> pair = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>(){});
            int result = this.stock.increaseProductsQuantity(pair.getFirst(), pair.getSecond());
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

    private void decreaseProductQuantity(Packet packet) {
        try {
            Pair<Integer, Integer> pair = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>(){});
            int result = this.stock.decreaseProductQuantity(pair.getFirst(), pair.getSecond());
            if(result > 0) {
                packet.setStatus(Status.SUCCESS);
                packet.setMessage(SUCCESS_MESSAGE);
            }
            else {
                packet.setStatus(Status.FAILURE);
                packet.setMessage(FAILURE_MESSAGE);
            }
        } catch (JsonProcessingException e) {
            packet.setStatus(Status.ERROR);
            packet.setMessage(ERROR_MESSAGE);
        }
    }

}
