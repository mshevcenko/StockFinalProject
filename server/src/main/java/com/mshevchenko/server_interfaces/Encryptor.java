package com.mshevchenko.server_interfaces;

import com.mshevchenko.packet.Packet;

public interface Encryptor {

    void encrypt(Packet packet);

}
