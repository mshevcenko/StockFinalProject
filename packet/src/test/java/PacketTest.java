import com.mshevchenko.crc16.CRC16Creator;
import com.mshevchenko.packet.Commands;
import com.mshevchenko.packet.Packet;
import com.mshevchenko.packet.Status;
import com.mshevchenko.packet.exceptions.LostDataException;
import com.mshevchenko.packet.exceptions.NotPacketException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PacketTest {

    private static final String ENCRYPTION_STRING = "RfUjXn2r5u8x/A%D*G-KaPdSgVkYp3s6";
    private static final SecretKey KEY = new SecretKeySpec(ENCRYPTION_STRING.getBytes(), "AES");

    private Packet packet;

    @BeforeEach
    public void setPacket() {
        String message = "{\"message\" : \"message\"}";
        this.packet = new Packet(1, Status.CLIENT, Commands.STOP, message);
    }

    @Test
    public void encryptPacketMagicTest() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        Assertions.assertEquals(Packet.MAGIC, bytes[0]);
    }

    @Test
    public void encryptPacketCRC16Test() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short crc16 = buffer.getShort(bytes.length-2);
        Assertions.assertEquals(CRC16Creator.createCRC16(bytes, 0, bytes.length-2), crc16);
    }

    @Test
    public void encryptPacketMessageEncryptedOneTest() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        byte[] encryptedMessage = Arrays.copyOfRange(bytes, 21, bytes.length - 2);
        byte[] originalMessage = this.packet.getMessage().getBytes();
        Assertions.assertFalse(Arrays.equals(encryptedMessage, originalMessage));
    }

    @Test
    public void encryptPacketMessageEncryptedTwoTest() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        byte[] encryptedMessage = Arrays.copyOfRange(bytes, 21, bytes.length - 2);
        byte[] originalMessage = this.packet.getMessage().getBytes();
        byte[] decryptedMessage;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            decryptedMessage = cipher.doFinal(encryptedMessage);
        }
        catch(Exception e) {
            decryptedMessage = encryptedMessage;
        }
        Assertions.assertArrayEquals(decryptedMessage, originalMessage);
    }

    @Test
    public void decryptPacketCheckMagicTest() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        bytes[0] = 0;
        Assertions.assertThrows(NotPacketException.class, () -> {
            Packet.decryptPacket(bytes);
        });
    }

    @Test
    public void decryptPacketCheckCRC16Test() {
        byte[] bytes = Packet.encryptPacket(this.packet);
        bytes[bytes.length - 2] = 0;
        bytes[bytes.length - 1] = 0;
        Assertions.assertThrows(LostDataException.class, () -> {
            Packet.decryptPacket(bytes);
        });
    }

    @Test
    public void decryptPacketTestCheckPacketEquals() throws LostDataException, NotPacketException {
        byte[] bytes = Packet.encryptPacket(this.packet);
        Packet testPacket = Packet.decryptPacket(bytes);
        Assertions.assertEquals(testPacket, this.packet);
    }

}
