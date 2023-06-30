package com.mshevchenko.server_interfaces;

import com.mshevchenko.packet.Packet;

public interface Processor {

    void process(Packet packet);

}
