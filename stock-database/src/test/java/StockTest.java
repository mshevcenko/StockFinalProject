import com.mshevchenko.stock.Stock;
import com.mshevchenko.stock_objects.Group;
import com.mshevchenko.stock_objects.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;

public class StockTest {

    private Stock stock;

    @BeforeEach
    public void prepareStock() throws SQLException, ClassNotFoundException {
        File f = new File("stock-database/src/main/resources/testStock.db");
        f.delete();
        this.stock = new Stock("stock-database/src/main/resources/testStock.db");
        this.stock.insertGroup(new Group(1, "Group1", "Group1"));
        this.stock.insertGroup(new Group(2, "Group2", "Group2"));
        this.stock.insertGroup(new Group(3, "Group3", "Group3"));
        this.stock.insertProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1.5, 30));
        this.stock.insertProduct(new Product(2, 1, "Product2", "Product2", "Product2", 2.5, 20));
        this.stock.insertProduct(new Product(3, 2, "Product3", "Product3", "Product3", 3.5, 10));
    }

    @AfterEach
    public void closeStock() throws SQLException {
        this.stock.close();
    }

    @Test
    public void insertGroupTest() {
        this.stock.insertGroup(new Group(1, "Group1", "Group1"));
        Assertions.assertEquals(this.stock.getGroups().size(), 3);
        this.stock.insertGroup(new Group(4, "Group4", "Group4"));
        Assertions.assertEquals(this.stock.getGroups().size(), 4);
    }

    @Test
    public void insertProductTest() {
        this.stock.insertProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
        this.stock.insertProduct(new Product(4, 1, "Product4", "Product4", "Product4", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 4);
    }

    @Test
    public void updateGroupTest() {
        this.stock.updateGroup(new Group(1, "TestGroup1", "Group1"));
        Assertions.assertEquals(this.stock.getGroupsByFilter(new Group(-1, "TestGroup1", null)).size(), 1);
    }

    @Test
    public void updateProductTest() {
        this.stock.updateProduct(new Product(1, 3, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 3, null, null, null, -1, -1)).size(), 1);
    }

    @Test
    public void deleteGroupTest() {
        this.stock.deleteGroup(new Group(1, "Group1", "Group1"));
        Assertions.assertEquals(this.stock.getGroups().size(), 2);
        Assertions.assertEquals(this.stock.getProducts().size(), 1);
    }

    @Test
    public void deleteProductTest() {
        this.stock.deleteProduct(new Product(1, 1, "Product1", "Product1", "Product1", 1, 1));
        Assertions.assertEquals(this.stock.getProducts().size(), 2);
    }

    @Test
    public void getGroupsTest() {
        Assertions.assertEquals(this.stock.getGroups().size(), 3);
    }

    @Test
    public void getProductsTest() {
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
    }

    @Test
    public void getGroupsByFilterTest() {
        Assertions.assertEquals(this.stock.getGroupsByFilter(new Group(-1, "%group%", null)).size(), 3);
        Assertions.assertEquals(this.stock.getGroupsByFilter(new Group(-1, "Group1", null)).size(), 1);
    }

    @Test
    public void getProductsByFilterTest() {
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 1, null, null, null, -1, -1)).size(), 2);
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 2, null, null, null, -1, -1)).size(), 1);
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 3, null, null, null, -1, -1)).size(), 0);
    }

    @Test
    public void getGroupByIdTest() {
        Assertions.assertEquals(this.stock.getGroupById(1), new Group(1, "Group1", "Group1"));
        Assertions.assertEquals(this.stock.getGroupById(4), null);
    }

    @Test
    public void getProductByIdTest() {
        Assertions.assertEquals(this.stock.getProductById(1), new Product(1, 1, "Product1", "Product1", "Product1", 1.5, 30));
        Assertions.assertEquals(this.stock.getProductById(4), null);
    }

    @Test
    public void deleteGroupByIdTest() {
        this.stock.deleteGroupById(1);
        Assertions.assertEquals(this.stock.getGroupById(1), null);
        Assertions.assertEquals(this.stock.getGroups().size(), 2);
    }

    @Test
    public void deleteGroupsByIdsTest() {
        this.stock.deleteGroupsByIds(new int[]{1, 2});
        Assertions.assertEquals(this.stock.getGroupById(1), null);
        Assertions.assertEquals(this.stock.getGroupById(2), null);
        Assertions.assertEquals(this.stock.getGroups().size(), 1);
    }

    @Test
    public void deleteProductByIdTest() {
        this.stock.deleteProductById(1);
        Assertions.assertEquals(this.stock.getProductById(1), null);
        Assertions.assertEquals(this.stock.getProducts().size(), 2);
    }

    @Test
    public void deleteProductsByIdsTest() {
        this.stock.deleteProductsByIds(new int[]{1, 2});
        Assertions.assertEquals(this.stock.getProductById(1), null);
        Assertions.assertEquals(this.stock.getProductById(2), null);
        Assertions.assertEquals(this.stock.getProducts().size(), 1);
    }

    @Test
    public void getProductsInnerJoinGroupsTest() {
        Assertions.assertEquals(this.stock.getProducts().size(), 3);
    }

    @Test
    public void getProductsInnerJoinGroupsByFilterTest() {
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 1, null, null, null, -1, -1)).size(), 2);
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 2, null, null, null, -1, -1)).size(), 1);
        Assertions.assertEquals(this.stock.getProductsByFilter(new Product(-1, 3, null, null, null, -1, -1)).size(), 0);
    }

    @Test
    public void increaseProductQuantityTest() {
        double previousQuantity = this.stock.getProductById(1).getQuantity();
        int result = this.stock.increaseProductQuantity(1, 10);
        Assertions.assertEquals(previousQuantity+10, this.stock.getProductById(1).getQuantity());
        Assertions.assertEquals(result, 1);
    }

    @Test
    public void increaseProductsQuantityTest() {
        double previousQuantity1 = this.stock.getProductById(1).getQuantity();
        double previousQuantity2 = this.stock.getProductById(2).getQuantity();
        int result = this.stock.increaseProductsQuantity(new int[]{1,2}, 10);
        Assertions.assertEquals(previousQuantity1+10, this.stock.getProductById(1).getQuantity());
        Assertions.assertEquals(previousQuantity2+10, this.stock.getProductById(2).getQuantity());
        Assertions.assertEquals(result, 2);
    }

    @Test
    public void decreaseProductQuantityTest() {
        double previousQuantity = this.stock.getProductById(1).getQuantity();
        int result = this.stock.decreaseProductQuantity(1, 10);
        Assertions.assertEquals(previousQuantity-10, this.stock.getProductById(1).getQuantity());
        Assertions.assertEquals(result, 1);
        result = this.stock.decreaseProductQuantity(1, 21);
        Assertions.assertEquals(previousQuantity-10, this.stock.getProductById(1).getQuantity());
        Assertions.assertEquals(result, 0);
    }

}
