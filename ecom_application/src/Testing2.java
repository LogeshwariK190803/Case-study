import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import entity.Customer;
import entity.Product;
import dao.OrderProcessorRepositoryImpl;
import java.util.List;
import java.util.Map;

public class Testing2 {

    private OrderProcessorRepositoryImpl orderProcessor;

    @Before
    public void setUp() {
        orderProcessor = new OrderProcessorRepositoryImpl();
    }

    @Test
    public void testAddSingleProductToCart() {
        Product product = new Product(5, "Watch", 1000.00, "Wireless Watch", 20);  
        Customer customer = new Customer(4, "Banu", "banu@gmail.com", "banu@123");
        boolean isAdded = orderProcessor.addToCart(customer, product, 2); 
        assertTrue("The product should be added to the cart successfully", isAdded);
        List<Map<Product, Integer>> cartItems = orderProcessor.getAllFromCart(customer);
        assertNotNull("Cart items should not be null", cartItems);
        assertFalse("Cart should contain products", cartItems.isEmpty());

        Map<Product, Integer> cartItem = cartItems.get(0);  
        Product cartProduct = cartItem.keySet().iterator().next();  
        int cartQuantity = cartItem.get(cartProduct);  

        assertEquals("Product name in the cart should be 'Watch'", "Watch", cartProduct.getName());
        assertEquals("Product quantity in the cart should be 2", 2, cartQuantity);  
    }
}
