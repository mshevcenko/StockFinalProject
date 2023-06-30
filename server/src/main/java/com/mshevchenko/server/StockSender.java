package com.mshevchenko.server;

import com.mshevchenko.server_interfaces.Sender;

import java.io.IOException;
import java.io.OutputStream;

public class StockSender implements Sender {

    private StockConnection connection;
    private OutputStream out;

    public StockSender(StockConnection connection, OutputStream out) throws IOException {
        this.connection = connection;
        this.out = out;
    }

    @Override
    public void send(byte[] packet) {
        try {
            this.out.write(packet);
        } catch (IOException e) {
            this.connection.close();
        }
    }

}
