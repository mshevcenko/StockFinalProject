package com.mshevchenko.server;


import com.mshevchenko.packet.Commands;
import com.mshevchenko.packet.Packet;
import com.mshevchenko.packet.Status;
import com.mshevchenko.packet.exceptions.LostDataException;
import com.mshevchenko.packet.exceptions.NotPacketException;
import com.mshevchenko.server_interfaces.Decryptor;
import com.mshevchenko.server_interfaces.Processor;

public class StockDecryptor implements Decryptor {

    private StockConnection connection;
    private Processor processor;

    public StockDecryptor(StockConnection connection, Processor processor) {
        this.connection = connection;
        this.processor = processor;
    }

    @Override
    public void decrypt(byte[] packet) {
        try {
            Packet p = Packet.decryptPacket(packet);
            this.processor.process(p);
        } catch (Exception e) {
            Packet p = new Packet(-1, Status.ERROR, Commands.UNKNOWN, "error");
            this.processor.process(p);
        }
    }

}
