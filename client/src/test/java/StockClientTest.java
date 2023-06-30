import com.mshevchenko.client.StockClient;
import com.mshevchenko.client.exceptions.InvalidQueryException;
import com.mshevchenko.client.exceptions.ServerErrorException;
import com.mshevchenko.client.exceptions.UnavailableServerException;
import com.mshevchenko.server.StockServer;
import com.mshevchenko.stock.Stock;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

public class StockClientTest {

    private Stock stock;
    private StockServer server;
    private StockClient client;

    @BeforeEach
    public void prepareStockServerClient() throws SQLException, ClassNotFoundException, IOException {
        File f = new File("client/src/main/resources/testStock.db");
        f.delete();
        this.stock = new Stock("client/src/main/resources/testStock.db");
        this.stock.insertGroup(new Group(1, "Group1", "Group1"));
        this.stock.insertGroup(new Group(2, "Group2", "Group2"));
        this.stock.insertGroup(new Group(3, "Group3", "Group3"));
        this.stock.insertProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1.5, 30));
        this.stock.insertProduct(new Product(2, 1, "Product2", "Product2", "Product2", 2.5, 20));
        this.stock.insertProduct(new Product(3, 2, "Product3", "Product3", "Product3", 3.5, 10));
        this.server = new StockServer(4545, this.stock);
        Thread serverThread = new Thread(this.server);
        serverThread.start();
        this.client = new StockClient(InetAddress.getLocalHost(), 4545);
    }

    @AfterEach
    public void closeStockServerClient() throws SQLException {
        this.stock.close();
        this.server.close();
        this.client.closeSocket();
        File f = new File("client/src/main/resources/testStock.db");
        f.delete();
    }

    @Test
    public void insertGroupTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        boolean result = this.client.insertGroup(new Group(1, "Group1", "Group1"));
        Assertions.assertEquals(this.client.getGroups().size(), 3);
        Assertions.assertFalse(result);
        result = this.client.insertGroup(new Group(4, "Group4", "Group4"));
        Assertions.assertTrue(result);
        Assertions.assertEquals(this.client.getGroups().size(), 4);
    }

    @Test
    public void insertProductTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        boolean result = this.client.insertProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
        Assertions.assertFalse(result);
        result = this.client.insertProduct(new Product(4, 1, "Product4", "Product4", "Product4", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 4);
        Assertions.assertTrue(result);
    }

    @Test
    public void updateGroupTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        boolean result = this.client.updateGroup(new Group(1, "TestGroup1", "Group1"));
        Assertions.assertEquals(this.stock.getGroupsByFilter(new Group(-1, "TestGroup1", null)).size(), 1);
        Assertions.assertTrue(result);
    }

    @Test
    public void updateProductTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        this.client.updateProduct(new Product(1, 3, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 3, null, null, null, -1, -1)).size(), 1);
    }

    @Test
    public void deleteGroupTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        this.client.deleteGroup(new Group(1, "Group1", "Group1"));
        Assertions.assertEquals(this.stock.getGroups().size(), 2);
        Assertions.assertEquals(this.stock.getProducts().size(), 1);
    }

    @Test
    public void deleteProductTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        this.client.deleteProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 2);
    }

    @Test
    public void getGroupsTest() throws UnavailableServerException, ServerErrorException {
        Assertions.assertEquals(this.client.getGroups().size(), 3);
    }

    @Test
    public void getProductsTest() {
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
    }

    @Test
    public void getGroupsByFilterTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        Assertions.assertEquals(this.client.getGroupsByFilter(new Group(-1, "%group%", null)).size(), 3);
        Assertions.assertEquals(this.client.getGroupsByFilter(new Group(-1, "Group1", null)).size(), 1);
    }

    @Test
    public void getProductsInnerJoinGroupsTest() {
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
    }

    @Test
    public void increaseProductQuantityTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        double previousQuantity = this.client.getProductById(1).getQuantity();
        boolean result = this.client.increaseProductQuantity(1, 10);
        Assertions.assertEquals(previousQuantity+10, this.client.getProductById(1).getQuantity());
        Assertions.assertTrue(result);
    }

    @Test
    public void increaseProductsQuantityTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        double previousQuantity1 = this.client.getProductById(1).getQuantity();
        double previousQuantity2 = this.client.getProductById(2).getQuantity();
        boolean result = this.client.increaseProductsQuantity(new int[]{1,2}, 10);
        Assertions.assertEquals(previousQuantity1+10, this.client.getProductById(1).getQuantity());
        Assertions.assertEquals(previousQuantity2+10, this.client.getProductById(2).getQuantity());
        Assertions.assertTrue(result);
    }

    @Test
    public void decreaseProductQuantityTest() throws UnavailableServerException, ServerErrorException, InvalidQueryException {
        double previousQuantity = this.client.getProductById(1).getQuantity();
        boolean result = this.client.decreaseProductQuantity(1, 10);
        Assertions.assertEquals(previousQuantity-10, this.client.getProductById(1).getQuantity());
        Assertions.assertTrue(result);
        result = this.client.decreaseProductQuantity(1, 21);
        Assertions.assertEquals(previousQuantity-10, this.client.getProductById(1).getQuantity());
        Assertions.assertFalse(result);
    }

}
