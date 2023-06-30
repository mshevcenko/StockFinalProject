package com.mshevchenko.packet;

import com.mshevchenko.crc16.CRC16Creator;
import com.mshevchenko.packet.exceptions.LostDataException;
import com.mshevchenko.packet.exceptions.NotPacketException;
import lombok.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Packet {

    public static byte MAGIC = 0x13;
    private static final String ENCRYPTION_STRING = "RfUjXn2r5u8x/A%D*G-KaPdSgVkYp3s6";
    private static final SecretKey KEY = new SecretKeySpec(ENCRYPTION_STRING.getBytes(), "AES");

    private long packetNumber;
    private int status;
    private int command;
    private String message;

    public static byte[] encryptPacket(Packet packet) {
        byte[] encryptedMessage = encryptMessage(packet.getMessage());
        ByteBuffer buffer = ByteBuffer.allocate(encryptedMessage.length + 23);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(MAGIC);
        buffer.putLong(packet.packetNumber);
        buffer.putInt(packet.getStatus());
        buffer.putInt(packet.getCommand());
        buffer.putInt(encryptedMessage.length);
        buffer.put(encryptedMessage);
        buffer.putShort(CRC16Creator.createCRC16(buffer.array(), 0, encryptedMessage.length + 21));
        return buffer.array();
    }

    private static byte[] encryptMessage(String message) {
        byte[] encryptedMessage;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            encryptedMessage = cipher.doFinal(message.getBytes());
        }
        catch(Exception e) {
            encryptedMessage = message.getBytes();
        }
        return encryptedMessage;
    }

    public static Packet decryptPacket(byte[] bytes) throws NotPacketException, LostDataException {
        if(!checkMagic(bytes)) {
            throw new NotPacketException();
        }
        if(!checkCRC16(bytes)) {
            throw new LostDataException();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.get();//magic
        long packetNumber = buffer.getLong();
        int status = buffer.getInt();
        int command = buffer.getInt();
        int messageLength = buffer.getInt();
        byte[] encryptedMessage = new byte[messageLength];
        buffer.get(encryptedMessage);
        String message = decryptMessage(encryptedMessage);
        return new Packet(packetNumber, status, command, message);
    }

    private static String decryptMessage(byte[] encryptedMessage) {
        byte[] message;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            message = cipher.doFinal(encryptedMessage);
        }
        catch(Exception e) {
            message = encryptedMessage;
        }
        return new String(message);
    }

    public static boolean checkCRC16(byte[] bytes) {
        if(bytes.length < 2) {
            return false;
        }
        short crc16 = ByteBuffer.wrap(new byte[] {bytes[bytes.length-2], bytes[bytes.length-1]}).getShort();
        return crc16 == CRC16Creator.createCRC16(bytes, 0, bytes.length-2);
    }

    public static boolean checkMagic(byte[] bytes) {
        return bytes.length > 0 && bytes[0] == MAGIC;
    }

}
