package com.mshevchenko.server;

import com.mshevchenko.stock.Stock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockServer implements Runnable {

    private int port;
    private ExecutorService executorService;
    private Stock stock;
    private List<StockConnection> connections;
    private ServerSocket serverSocket;

    public StockServer(int port, Stock stock) throws IOException {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.connections = new LinkedList<>();
        this.port = port;
        this.stock = stock;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        while(!this.serverSocket.isClosed()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                StockConnection connection = new StockConnection(this, clientSocket);
                this.connections.add(connection);
                this.executorService.execute(connection);
            } catch (IOException e) {
                close();
            }
        }
    }

    public void close() {
        for(StockConnection connection : this.connections) {
            connection.close();
        }
        this.executorService.shutdownNow();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            this.serverSocket = null;
        }
    }

    public void removeConnection(StockConnection connection) {
        this.connections.remove(connection);
    }

    public List<StockConnection> getConnections() {
        return connections;
    }

    public Stock getStock() {
        return stock;
    }

}
