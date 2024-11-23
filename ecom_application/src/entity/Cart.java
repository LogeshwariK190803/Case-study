package entity;

import java.util.List;

public class Cart {
    private int cartId;
    private int customerId;
    private List<CartItem> items;  // List of cart items

    public Cart(int cartId, int customerId, List<CartItem> items) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.items = items;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
