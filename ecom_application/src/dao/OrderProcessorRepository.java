package dao;

import entity.Customer;
import entity.Product;

import java.util.List;
import java.util.Map;

public interface OrderProcessorRepository {

    boolean createProduct(Product product);
    boolean createCustomer(Customer customer);
    boolean deleteProduct(int productId);
    boolean deleteCustomer(int customerId);
    boolean addToCart(Customer customer, Product product, int quantity);
    boolean removeFromCart(Customer customer, Product product);
    List<Map<Product, Integer>> getAllFromCart(Customer customer);
    boolean placeOrder(Customer customer, List<Map<Product, Integer>> products, String shippingAddress);
    List<Map<Product, Integer>> getOrdersByCustomer(int customerId);
}
