package com.mshevchenko;

import com.mshevchenko.client.StockClient;
import com.mshevchenko.gui.MainFrame;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {
        StockClient client = new StockClient(InetAddress.getLocalHost(), 4545);
        MainFrame frame = new MainFrame(client);
        frame.setVisible(true);
        frame.setDisplayedTable(MainFrame.DisplayedTable.GROUPS);
    }

}
