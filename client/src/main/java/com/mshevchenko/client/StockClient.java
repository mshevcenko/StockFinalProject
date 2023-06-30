package com.mshevchenko.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshevchenko.client.exceptions.InvalidQueryException;
import com.mshevchenko.client.exceptions.ServerErrorException;
import com.mshevchenko.client.exceptions.UnavailableServerException;
import com.mshevchenko.packet.Commands;
import com.mshevchenko.packet.Packet;
import com.mshevchenko.packet.Status;
import com.mshevchenko.packet.exceptions.LostDataException;
import com.mshevchenko.packet.exceptions.NotPacketException;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Pair;
import com.mshevchenko.stock_objects.Product;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

public class StockClient {

    private InetAddress serverAddress;
    private int serverPort;
    private Socket socket;
    private boolean closed;
    private InputStream in;
    private OutputStream out;
    private long packetNumber = 0;
    private ObjectMapper objectMapper;
    private int timeout = 1000;

    public StockClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.closed = false;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(int command, String message) throws UnavailableServerException {
        Packet packet = new Packet(this.packetNumber, Status.CLIENT, command, message);
        sendPacket(packet);
    }

    public void sendPacket(Packet packet) throws UnavailableServerException {
        if(this.socket == null || this.socket.isClosed()) {
            connectToServer();
        }
        byte[] bytes = Packet.encryptPacket(packet);
        /*for(byte b : bytes) {
            System.out.print((int)b + " ");
        }
        System.out.println();*/
        try {
            this.out.write(bytes);
            this.packetNumber++;
        } catch (IOException e) {
            closeSocket();
            resend(bytes);
        }
    }

    public Packet receivePacket() throws UnavailableServerException, ServerErrorException {
        if(this.socket == null || this.socket.isClosed()) {
            connectToServer();
        }
        try {
            int magic = this.in.read();
            if(magic != Packet.MAGIC) {
                closeSocket();
            }
            else {
                byte[] packetStart = new byte[16];
                this.in.read(packetStart, 0, packetStart.length);
                byte[] messageLength = new byte[4];
                this.in.read(messageLength, 0, messageLength.length);
                int mesLength = ByteBuffer.wrap(messageLength).getInt();
                byte[] packet = new byte[mesLength + 23];
                packet[0] = Packet.MAGIC;
                System.arraycopy(packetStart, 0, packet, 1, packetStart.length);
                System.arraycopy(messageLength, 0, packet, 1 + packetStart.length, messageLength.length);
                this.in.read(packet, 21, mesLength + 2);
                return Packet.decryptPacket(packet);
            }
        } catch (IOException e) {
            closeSocket();
            return rereceivePacket();
        } catch (Exception ignored) {
        }
        throw new ServerErrorException();
    }

    public Packet rereceivePacket() throws UnavailableServerException, ServerErrorException {
        if(this.socket == null || this.socket.isClosed()) {
            connectToServer();
        }
        try {
            int magic = this.in.read();
            if(magic != Packet.MAGIC) {
                closeSocket();
            }
            else {
                byte[] packetStart = new byte[16];
                this.in.read(packetStart, 0, packetStart.length);
                byte[] messageLength = new byte[4];
                this.in.read(messageLength, 0, messageLength.length);
                int mesLength = ByteBuffer.wrap(messageLength).getInt();
                byte[] packet = new byte[mesLength + 23];
                packet[0] = Packet.MAGIC;
                System.arraycopy(packetStart, 0, packet, 1, packetStart.length);
                System.arraycopy(messageLength, 0, packet, 1 + packetStart.length, messageLength.length);
                this.in.read(packet, 21, mesLength + 2);
                return Packet.decryptPacket(packet);
            }
        } catch (IOException e) {
            closeSocket();
            throw new UnavailableServerException();
        } catch (Exception ignored) {
        }
        throw new ServerErrorException();
    }

    private void resend(byte[] bytes) throws UnavailableServerException {
        if(this.socket == null || this.socket.isClosed()) {
            connectToServer();
        }
        try {
            this.out.write(bytes);
            this.packetNumber++;
        } catch (IOException e) {
            closeSocket();
            throw new UnavailableServerException();
        }
    }

    private void connectToServer() throws UnavailableServerException {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(this.serverAddress, this.serverPort), this.timeout);
            this.socket.setSoTimeout(this.timeout);
            this.in = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
        } catch (IOException e) {
            closeSocket();
            throw new UnavailableServerException();
        }
    }

    public void closeSocket() {
        try {
            if(this.socket != null) {
                this.socket.close();
            }
            if(this.in != null) {
                this.in.close();
            }
            if(this.out != null) {
                this.out.close();
            }
        } catch (IOException e) {
            this.socket = null;
            this.in = null;
            this.out = null;
        }
    }

    public boolean insertGroup(Group group) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(group);
            sendMessage(Commands.INSERT_GROUP, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean insertProduct(Product product) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(product);
            sendMessage(Commands.INSERT_PRODUCT, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean updateGroup(Group group) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(group);
            sendMessage(Commands.UPDATE_GROUP, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean updateProduct(Product product) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(product);
            sendMessage(Commands.UPDATE_PRODUCT, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteGroup(Group group) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(group);
            sendMessage(Commands.DELETE_GROUP, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteProduct(Product product) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(product);
            sendMessage(Commands.DELETE_PRODUCT, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteGroupById(int id) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(id);
            sendMessage(Commands.DELETE_GROUP_BY_ID, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteProductById(int id) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(id);
            sendMessage(Commands.DELETE_PRODUCT_BY_ID, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteGroupsByIds(int[] ids) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(ids);
            sendMessage(Commands.DELETE_GROUPS_BY_IDS, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteProductsByIds(int[] ids) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            String json = this.objectMapper.writeValueAsString(ids);
            sendMessage(Commands.DELETE_PRODUCTS_BY_IDS, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                System.out.println(packet.getStatus());
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Group> getGroups() throws UnavailableServerException, ServerErrorException {
        sendMessage(Commands.GET_GROUPS, "");
        Packet packet = receivePacket();
        if(packet.getStatus() == Status.SUCCESS) {
            try {
                List<Group> groups = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                return groups;
            } catch (JsonProcessingException e) {
                throw new ServerErrorException();
            }
        }
        else {
            throw new ServerErrorException();
        }
    }

    public List<Product> getProducts() throws UnavailableServerException, ServerErrorException {
        sendMessage(Commands.GET_PRODUCTS, "");
        Packet packet = receivePacket();
        if(packet.getStatus() == Status.SUCCESS) {
            try {
                List<Product> products = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                return products;
            } catch (JsonProcessingException e) {
                throw new ServerErrorException();
            }
        }
        else {
            throw new ServerErrorException();
        }
    }

    public List<Group> getGroupsByFilter(Group group) throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        try {
            String json = this.objectMapper.writeValueAsString(group);
            sendMessage(Commands.GET_GROUPS_BY_FILTER, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                try {
                    List<Group> groups = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                    return groups;
                } catch (JsonProcessingException e) {
                    throw new ServerErrorException();
                }
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Product> getProductsByFilter(Product product) throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        try {
            String json = this.objectMapper.writeValueAsString(product);
            sendMessage(Commands.GET_PRODUCTS_BY_FILTER, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                try {
                    List<Product> products = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                    return products;
                } catch (JsonProcessingException e) {
                    throw new ServerErrorException();
                }
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<String[]> getProductsInnerJoinGroups() throws UnavailableServerException, ServerErrorException {
        sendMessage(Commands.GET_PRODUCTS_INNER_JOIN_GROUPS, "");
        Packet packet = receivePacket();
        if(packet.getStatus() == Status.SUCCESS) {
            try {
                List<String[]> products = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                return products;
            } catch (JsonProcessingException e) {
                throw new ServerErrorException();
            }
        }
        else {
            throw new ServerErrorException();
        }
    }

    public List<String[]> getProductsInnerJoinGroupsByFilter(Product product) throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        try {
            String json = this.objectMapper.writeValueAsString(product);
            sendMessage(Commands.GET_PRODUCTS_INNER_JOIN_GROUPS_BY_FILTER, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                try {
                    List<String[]> products = this.objectMapper.readValue(packet.getMessage(), new TypeReference<>() {});
                    return products;
                } catch (JsonProcessingException e) {
                    throw new ServerErrorException();
                }
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public Group getGroupById(int id) throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        try {
            String json = this.objectMapper.writeValueAsString(id);
            sendMessage(Commands.GET_GROUP_BY_ID, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                try {
                    Group group = this.objectMapper.readValue(packet.getMessage(), Group.class);
                    return group;
                } catch (JsonProcessingException e) {
                    throw new ServerErrorException();
                }
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public Product getProductById(int id) throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        try {
            String json = this.objectMapper.writeValueAsString(id);
            sendMessage(Commands.GET_PRODUCT_BY_ID, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                try {
                    Product product = this.objectMapper.readValue(packet.getMessage(), Product.class);
                    return product;
                } catch (JsonProcessingException e) {
                    throw new ServerErrorException();
                }
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean increaseProductQuantity(int id, int quantity) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            Pair<Integer, Integer> pair = new Pair<>(id, quantity);
            String json = this.objectMapper.writeValueAsString(pair);
            sendMessage(Commands.INCREASE_PRODUCT_QUANTITY, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean increaseProductsQuantity(int[] ids, int quantity) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            Pair<int[], Integer> pair = new Pair<>(ids, quantity);
            String json = this.objectMapper.writeValueAsString(pair);
            sendMessage(Commands.INCREASE_PRODUCTS_QUANTITY, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean decreaseProductQuantity(int id, int quantity) throws UnavailableServerException, InvalidQueryException, ServerErrorException {
        try {
            Pair<Integer, Integer> pair = new Pair<>(id, quantity);
            String json = this.objectMapper.writeValueAsString(pair);
            sendMessage(Commands.DECREASE_PRODUCT_QUANTITY, json);
            Packet packet = receivePacket();
            if(packet.getStatus() == Status.SUCCESS) {
                return true;
            }
            else if(packet.getStatus() == Status.FAILURE) {
                return false;
            }
            else {
                throw new ServerErrorException();
            }
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public void stop() {
        try {
            sendMessage(Commands.STOP, "stop");
        } catch (UnavailableServerException ignored) {
        }
    }

    /*public void sendMessage(int command, String message) throws InterruptedException {
        Packet response;
        do {
            while (this.socket == null) {
                try {
                    this.socket = new Socket(this.serverAddress, this.serverPort);
                    this.in = this.socket.getInputStream();
                    this.out = this.socket.getOutputStream();
                    this.packetNumber = 0;
                } catch (IOException e) {
                    Thread.sleep(1000);
                }
            }
            Message mes = new Message(command, this.user, message);
            Packet packet = new Packet(this.source, this.packetNumber++, mes);
            sendPacket(packet);
            response = receive();
        } while(this.socket == null);
        return answer;
    }

    private void sendPacket(Packet packet) {
        try {
            out.write(Packet.encryptPacket(packet));
        } catch (IOException e) {
            this.socket = null;
        }
    }

    private Packet receive() {
        Packet packet = null;
        try {
            int magic = this.in.read();
            if(magic == -1) {
                this.socket.close();
                this.socket = null;
            }
            else if(magic == 0x13) {
                byte[] packetStart = new byte[9];
                this.in.read(packetStart, 0, packetStart.length);
                byte[] messageLength = new byte[4];
                this.in.read(messageLength, 0, messageLength.length);
                int mesLength = ByteBuffer.wrap(messageLength).getInt();
                byte[] p = new byte[mesLength + 18];
                p[0] = 0x13;
                System.arraycopy(packetStart, 0, p, 1, packetStart.length);
                System.arraycopy(messageLength, 0, p, 10, messageLength.length);
                this.in.read(p, 14, mesLength + 4);
                packet = Packet.decryptPacket(p);
            }
        } catch (IOException e) {
            this.socket = null;
        } catch (LostDataException e) {
            throw new RuntimeException(e);
        } catch (NotPacketException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }

    public void close() throws IOException {
        this.closed = true;
        this.socket.close();
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean insertGroup(Group group) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(group);
            Packet packet = sendMessage(Commands.INSERT_GROUP, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean insertProduct(Product product) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(product);
            Packet packet = sendMessage(StockCommands.INSERT_PRODUCT, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean updateGroup(Group group) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(group);
            Packet packet = sendMessage(StockCommands.UPDATE_GROUP, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean updateProduct(Product product) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(product);
            Packet packet = sendMessage(StockCommands.UPDATE_PRODUCT, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteGroup(Group group) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(group);
            Packet packet = sendMessage(StockCommands.DELETE_GROUP, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public boolean deleteProduct(Product product) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(product);
            Packet packet = sendMessage(StockCommands.DELETE_PRODUCT, query);
            return packet.getMessage().getMessage().equals("success");
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Group> getGroups() throws UnavailableServerException, InvalidQueryException {
        try {
            Packet packet = sendMessage(StockCommands.GET_GROUPS, "");
            List<Group> groups = this.objectMapper.readValue(packet.getMessage().getMessage(), new TypeReference<>() {});
            return groups;
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Product> getProducts() throws UnavailableServerException, InvalidQueryException {
        try {
            Packet packet = sendMessage(StockCommands.GET_PRODUCTS, "");
            List<Product> products = this.objectMapper.readValue(packet.getMessage().getMessage(), new TypeReference<>() {});
            return products;
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Group> getGroupsByFilter(Group group) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(group);
            Packet packet = sendMessage(StockCommands.GET_GROUPS_BY_FILTER, query);
            List<Group> groups = this.objectMapper.readValue(packet.getMessage().getMessage(), new TypeReference<>() {});
            return groups;
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }

    public List<Product> getProductsByFilter(Product product) throws UnavailableServerException, InvalidQueryException {
        try {
            String query = this.objectMapper.writeValueAsString(product);
            Packet packet = sendMessage(StockCommands.GET_PRODUCTS_BY_FILTER, query);
            List<Product> products = this.objectMapper.readValue(packet.getMessage().getMessage(), new TypeReference<>() {});
            return products;
        } catch (InterruptedException e) {
            throw new UnavailableServerException();
        } catch (JsonProcessingException e) {
            throw new InvalidQueryException();
        }
    }*/

}
