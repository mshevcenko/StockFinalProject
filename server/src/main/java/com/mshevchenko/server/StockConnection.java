package com.mshevchenko.server;

import com.mshevchenko.server_interfaces.*;
import com.mshevchenko.stock.Stock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StockConnection implements Runnable {

    private StockServer server;
    private Receiver receiver;
    private Decryptor decryptor;
    private Processor processor;
    private Encryptor encryptor;
    private Sender sender;
    private InputStream in;
    private OutputStream out;
    private Socket socket;

    public StockConnection(StockServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.in = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
            this.sender = new StockSender(this, this.out);
            this.encryptor = new StockEncryptor(this, this.sender);
            this.processor = new StockProcessor(this, this.encryptor, this.server.getStock());
            this.decryptor = new StockDecryptor(this, this.processor);
            this.receiver = new StockReceiver(this, this.decryptor, this.in);
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void run() {
        while(!this.socket.isClosed()) {
            this.receiver.receiveMessage();
        }
    }

    public void close() {
        server.removeConnection(this);
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

    public Socket getSocket() {
        return socket;
    }
}
