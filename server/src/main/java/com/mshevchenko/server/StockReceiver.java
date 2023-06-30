package com.mshevchenko.server;

import com.mshevchenko.packet.Packet;
import com.mshevchenko.server_interfaces.Receiver;
import com.mshevchenko.server_interfaces.Decryptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StockReceiver implements Receiver {

    private StockConnection connection;
    private Decryptor decryptor;
    private InputStream in;

    public StockReceiver(StockConnection connection, Decryptor decryptor, InputStream in) throws IOException {
        this.connection = connection;
        this.decryptor = decryptor;
        this.in = in;
    }

    @Override
    public void receiveMessage() {
        try {
            int magic = this.in.read();
            if(magic != Packet.MAGIC) {
                this.connection.close();
            }
            else {
                readPacket();
            }
        } catch (IOException e) {
            this.connection.close();
        }
    }

    private void readPacket() throws IOException {
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
        this.decryptor.decrypt(packet);
    }

}
