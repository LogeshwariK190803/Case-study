import dao.OrderProcessorRepositoryImpl;
import entity.Customer;
import entity.Product;

import java.util.*;

public class EcomApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OrderProcessorRepositoryImpl orderProcessor = new OrderProcessorRepositoryImpl();
        
        while (true) {
            System.out.println("\n===== E-Commerce Application Menu =====");
            System.out.println("1. Register Customer");
            System.out.println("2. Create Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Customer Order");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            switch (choice) {
                case 1: 
                    System.out.print("Enter Customer Name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter Customer Email: ");
                    String customerEmail = scanner.nextLine();
                    System.out.print("Enter Customer Password: ");
                    String customerPassword = scanner.nextLine();
                    
                    Customer newCustomer = new Customer(0, customerName, customerEmail, customerPassword);
                    boolean customerRegistered = orderProcessor.createCustomer(newCustomer);
                    System.out.println("Customer Registration " + (customerRegistered ? "Successful" : "Failed"));
                    break;

                case 2:
                    System.out.print("Enter Product Name: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter Product Description: ");
                    String productDescription = scanner.nextLine();
                    System.out.print("Enter Product Price: ");
                    double productPrice = scanner.nextDouble();
                    System.out.print("Enter Product Stock Quantity: ");
                    int productStock = scanner.nextInt();
                    scanner.nextLine(); 
                    Product newProduct = new Product(0, productName, productPrice, productDescription, productStock);
                    boolean productCreated = orderProcessor.createProduct(newProduct);
                    System.out.println("Product Creation " + (productCreated ? "Successful" : "Failed"));
                    break;

                case 3: 
                    System.out.print("Enter Product ID to Delete: ");
                    int productIdToDelete = scanner.nextInt();
                    scanner.nextLine();  
                    
                    boolean productDeleted = orderProcessor.deleteProduct(productIdToDelete);
                    System.out.println("Product Deletion " + (productDeleted ? "Successful" : "Failed"));
                    break;

                case 4:
                    System.out.print("Enter Customer ID: ");
                    int customerIdForCart = scanner.nextInt();
                    System.out.print("Enter Product ID: ");
                    int productIdForCart = scanner.nextInt();
                    System.out.print("Enter Quantity: ");
                    int quantityForCart = scanner.nextInt();
                    scanner.nextLine();  
                    
                    Customer customerForCart = new Customer(customerIdForCart, "", "", "");
                    Product productForCart = new Product(productIdForCart, "", 0, "", 0);
                    boolean addedToCart = orderProcessor.addToCart(customerForCart, productForCart, quantityForCart);
                    System.out.println("Product Added to Cart: " + (addedToCart ? "Successful" : "Failed"));
                    break;

                case 5:
                	System.out.print("Enter Customer ID to View Cart: ");
                	int customerIdForViewCart = scanner.nextInt();
                	scanner.nextLine();  // Consume the newline character

                	Customer customerForViewCart = new Customer(customerIdForViewCart, "", "", "");
                	List<Map<Product, Integer>> cartItems = orderProcessor.getAllFromCart(customerForViewCart);

                	System.out.println("Products in Cart:");
                	if (cartItems.isEmpty()) {
                	    System.out.println("Cart is empty.");
                	} else {
                	    for (Map<Product, Integer> item : cartItems) {
                	        for (Map.Entry<Product, Integer> entry : item.entrySet()) {
                	            Product product = entry.getKey();
                	            int quantity = entry.getValue();
                	            System.out.println(product.getName() + " - Quantity: " + quantity);
                	        }
                	    }
                	}
                	break;

                case 6:
                    System.out.print("Enter Customer ID for Order: ");
                    int customerIdForOrder = scanner.nextInt();
                    scanner.nextLine(); 
                    
                    Customer customerForOrder = new Customer(customerIdForOrder, "", "", "");
                    List<Map<Product, Integer>> productsForOrder = new ArrayList<>();
                    
                    while (true) {
                        System.out.print("Enter Product ID for Order (0 to stop): ");
                        int productId = scanner.nextInt();
                        if (productId == 0) break; // Stop when ID is 0
                        System.out.print("Enter Quantity: ");
                        int quantity = scanner.nextInt();
                        
                        Product productForOrder = new Product(productId, "", 0, "", 0);
                        Map<Product, Integer> orderItem = new HashMap<>();
                        orderItem.put(productForOrder, quantity);
                        productsForOrder.add(orderItem);
                    }
                    
                    System.out.print("Enter Shipping Address: ");
                    scanner.nextLine();  
                    String shippingAddress = scanner.nextLine();
                    
                    boolean orderPlaced = orderProcessor.placeOrder(customerForOrder, productsForOrder, shippingAddress);
                    System.out.println("Order " + (orderPlaced ? "Placed" : "Failed"));
                    break;

                case 7:
                    System.out.print("Enter Customer ID to View Order: ");
                    int customerIdForViewOrder = scanner.nextInt();
                    scanner.nextLine();  
                    
                    Customer customerForViewOrder = new Customer(customerIdForViewOrder, "", "", "");
                    List<Map<Product, Integer>> orders = orderProcessor.getOrdersByCustomer(customerForViewOrder.getCustomerId());
                    
                    System.out.println("Orders for Customer (ID: " + customerIdForViewOrder + "):");
                    for (Map<Product, Integer> order : orders) {
                        for (Map.Entry<Product, Integer> entry : order.entrySet()) {
                            Product p = entry.getKey();
                            int quantity = entry.getValue();
                            System.out.println(p.getName() + " - Quantity: " + quantity);
                        }
                    }
                    break;

                case 8: 
                    System.out.println("Exiting the Application.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
