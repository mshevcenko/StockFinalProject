package com.mshevchenko.server;

import com.mshevchenko.packet.Packet;
import com.mshevchenko.server_interfaces.Encryptor;
import com.mshevchenko.server_interfaces.Sender;

public class StockEncryptor implements Encryptor {

    private StockConnection connection;
    private Sender sender;

    public StockEncryptor(StockConnection connection, Sender sender) {
        this.connection = connection;
        this.sender = sender;
    }

    @Override
    public void encrypt(Packet packet) {
        this.sender.send(Packet.encryptPacket(packet));
    }
}
