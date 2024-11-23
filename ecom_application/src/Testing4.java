import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import exception.CustomerNotFoundException;
import exception.ProductNotFoundException;
import exception.OrderNotFoundException;

public class Testing4 {

    @Test
    public void testCustomerNotFoundException() {
        String invalidCustomerId = "100"; 

        assertThrows(CustomerNotFoundException.class, () -> {
            findCustomerById(invalidCustomerId);
        });
    }

    private void findCustomerById(String customerId) throws CustomerNotFoundException {
        String[] existingCustomerIds = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        
        boolean customerExists = false;
        for (String id : existingCustomerIds) {
            if (id.equals(customerId)) {
                customerExists = true;
                break;
            }
        }
        
        if (!customerExists) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }
    }

    @Test
    public void testProductNotFoundException() {
        String invalidProductId = "100"; 

        assertThrows(ProductNotFoundException.class, () -> {
            findProductById(invalidProductId);
        });
    }

    private void findProductById(String productId) throws ProductNotFoundException {
        String[] existingProductIds = {"5", "6", "7", "8", "9", "11", "12", "13", "14", "16", "17", "18", "19"};
        
        boolean productExists = false;
        for (String id : existingProductIds) {
            if (id.equals(productId)) {
                productExists = true;
                break;
            }
        }
        
        if (!productExists) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Test
    public void testOrderNotFoundException() {
        String invalidOrderId = "100";

        assertThrows(OrderNotFoundException.class, () -> {
            findOrderById(invalidOrderId);
        });
    }

    private void findOrderById(String orderId) throws OrderNotFoundException {
        String[] existingOrderIds = {"9", "10", "11"};
        
        boolean orderExists = false;
        for (String id : existingOrderIds) {
            if (id.equals(orderId)) {
                orderExists = true;
                break;
            }
        }
        
        if (!orderExists) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }
    }
}
