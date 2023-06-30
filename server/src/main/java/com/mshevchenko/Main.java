package com.mshevchenko;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshevchenko.server.StockServer;
import com.mshevchenko.stock.Stock;
import com.mshevchenko.stock_objects.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Stock stock = new Stock("database/StockDataBase.db");
        StockServer server = new StockServer(4545, stock);
        Thread serverThread = new Thread(server);
        serverThread.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String value = "";
        do {
            value = reader.readLine();
            if(value.equals("connections")) {
                System.out.println(server.getConnections().size());
            }
        } while(!value.equals("stop"));
        server.close();
    }

}
