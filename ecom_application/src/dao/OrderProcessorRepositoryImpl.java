package dao;

import entity.Customer;
import entity.Product;
import util.DBConnUtil;

import java.sql.*;
import java.util.*;

public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {
    private Connection connection = DBConnUtil.getCon();

    @Override
    public boolean createProduct(Product product) {
        String sql = "INSERT INTO products (name, price, description, stock_quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setString(3, product.getDescription());
            statement.setInt(4, product.getStockQuantity());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        // SQL queries to delete from related tables and the products table
        String deleteOrderItemsSql = "DELETE FROM order_items WHERE product_id = ?";
        String deleteCartSql = "DELETE FROM cart WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM products WHERE product_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteOrderItemsStmt = connection.prepareStatement(deleteOrderItemsSql);
                 PreparedStatement deleteCartStmt = connection.prepareStatement(deleteCartSql);
                 PreparedStatement deleteProductStmt = connection.prepareStatement(deleteProductSql)) {

                deleteOrderItemsStmt.setInt(1, productId);
                deleteOrderItemsStmt.executeUpdate();

                deleteCartStmt.setInt(1, productId);
                deleteCartStmt.executeUpdate();

                deleteProductStmt.setInt(1, productId);
                deleteProductStmt.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Override
    public boolean addToCart(Customer customer, Product product, int quantity) {
        String sql = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            stmt.setInt(3, quantity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Override
    public boolean removeFromCart(Customer customer, Product product) {
        String sql = "DELETE FROM cart WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<Product, Integer>> getAllFromCart(Customer customer) {
        String sql = "SELECT p.product_id, p.name, p.price, p.description, p.stock_quantity, c.quantity " +
                     "FROM cart c " +
                     "INNER JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.customer_id = ?";
        List<Map<Product, Integer>> cartItems = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create a Product object with details from the products table
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stock_quantity")
                );

                int quantity = rs.getInt("quantity"); // Quantity from the cart table

                // Map the product to its quantity
                Map<Product, Integer> cartItem = new HashMap<>();
                cartItem.put(product, quantity);

                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cartItems;
    }


    
    public void viewCart(Customer customer) {
        List<Map<Product, Integer>> cartItems = getAllFromCart(customer);

        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("Products in Cart:");
        for (Map<Product, Integer> item : cartItems) {
            for (Map.Entry<Product, Integer> entry : item.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                System.out.println(product.getName() + " - Quantity: " + quantity);
            }
        }
    }

    
    public Product getProductById(int productId){
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stock_quantity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    
    @Override
    public boolean placeOrder(Customer customer, List<Map<Product, Integer>> products, String shippingAddress) {
        String orderSql = "INSERT INTO orders (customer_id, order_date, shipping_address, total_price) VALUES (?, CURRENT_TIMESTAMP, ?, ?)";
        String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
        double totalPrice = 0.0;

        try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderItemStmt = connection.prepareStatement(orderItemSql)) {

            // Calculate the total price based on products in the cart
            for (Map<Product, Integer> map : products) {
                for (Map.Entry<Product, Integer> entry : map.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();

                    // Fetch the full product details from the database
                    Product fetchedProduct = getProductById(product.getProductId());
                    if (fetchedProduct != null) {
                        totalPrice += fetchedProduct.getPrice() * quantity;  
                    } else {
                        System.out.println("Product with ID " + product.getProductId() + " does not exist in the database.");
                        return false; 
                    }
                }
            }

            System.out.println("Total Price: " + totalPrice);

            // Insert the order
            orderStmt.setInt(1, customer.getCustomerId());
            orderStmt.setString(2, shippingAddress);
            orderStmt.setDouble(3, totalPrice);
            orderStmt.executeUpdate();

            // Get the generated order ID
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);

                // Insert order items
                for (Map<Product, Integer> map : products) {
                    for (Map.Entry<Product, Integer> entry : map.entrySet()) {
                        orderItemStmt.setInt(1, orderId);
                        orderItemStmt.setInt(2, entry.getKey().getProductId());
                        orderItemStmt.setInt(3, entry.getValue());
                        orderItemStmt.addBatch();
                    }
                }
                orderItemStmt.executeBatch();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

  


    @Override
    public List<Map<Product, Integer>> getOrdersByCustomer(int customerId) {
        String sql = "SELECT oi.product_id, oi.quantity, p.name, p.price, p.description, p.stock_quantity " +
                     "FROM orders o INNER JOIN order_items oi ON o.order_id = oi.order_id " +
                     "INNER JOIN products p ON oi.product_id = p.product_id WHERE o.customer_id = ?";
        List<Map<Product, Integer>> orders = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity")
                );
                Map<Product, Integer> orderItem = new HashMap<>();
                orderItem.put(product, rs.getInt("quantity"));
                orders.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
