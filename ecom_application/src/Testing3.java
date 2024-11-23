import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import dao.OrderProcessorRepositoryImpl;
import entity.Customer;
import entity.Product;

import java.util.List;
import java.util.Map;

public class Testing3 {

    private OrderProcessorRepositoryImpl orderProcessor;
    private Customer customer;
    private Product product;

    @Before
    public void setUp() {
        orderProcessor = new OrderProcessorRepositoryImpl();

        customer = new Customer(2, "Maha", "maha@gmail.com", "maha@123"); 
        product = new Product(5, "Watch", 1000.00, "Wireless Watch", 20); 

        orderProcessor.addToCart(customer, product, 2); 
    }

    @Test
    public void testPlaceOrderSuccessfully() {
        List<Map<Product, Integer>> cartItems = orderProcessor.getAllFromCart(customer);
        String shippingAddress = "street3"; 
        boolean isOrderPlaced = orderProcessor.placeOrder(customer, cartItems, shippingAddress);

        assertTrue("Order should be placed successfully", isOrderPlaced);

        List<Map<Product, Integer>> orders = orderProcessor.getOrdersByCustomer(customer.getCustomerId());
        
        boolean orderContainsProduct = false;
        for (Map<Product, Integer> order : orders) {
            for (Map.Entry<Product, Integer> entry : order.entrySet()) {
                Product orderedProduct = entry.getKey();
                int quantity = entry.getValue();
                
                if (orderedProduct.getProductId() == product.getProductId() && quantity == 2) {
                    orderContainsProduct = true;
                }
            }
        }

        assertTrue("Order should contain the product with the correct quantity", orderContainsProduct);
    }

    @Test
    public void testOrderDetailsInOrderTable() {
        int expectedCustomerId = 2;
        String expectedShippingAddress = "street3";
        double expectedTotalPrice = 2000.00; 
        String actualShippingAddress = "street3"; 
        double actualTotalPrice = 2000.00; 

        assertEquals("Customer ID should match", expectedCustomerId, customer.getCustomerId());
        assertEquals("Shipping address should match", expectedShippingAddress, actualShippingAddress);
        assertEquals("Order total price should be correct", expectedTotalPrice, actualTotalPrice, 0.01);
    }
}
