import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dao.OrderProcessorRepositoryImpl;
import entity.Product;

class Testing {

    private OrderProcessorRepositoryImpl orderProcessor;

    @BeforeEach
    void setUp() {
        orderProcessor = new OrderProcessorRepositoryImpl();
    }

    @Test
    void testCreateProduct() {
        Product product = new Product(0, "Test Product", 99.99, "Test Description", 10);
        boolean isCreated = orderProcessor.createProduct(product);
        assertTrue(isCreated, "Product should be created successfully.");
    }

}
