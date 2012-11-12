package com.pyxis.petstore.domain.order;

import com.pyxis.petstore.domain.billing.PaymentMethod;
import com.pyxis.petstore.domain.product.ItemInventory;
import com.pyxis.petstore.domain.product.ItemNumber;
import org.testinfected.petstore.QueryUnitOfWork;
import org.testinfected.petstore.Transactor;

import java.math.BigDecimal;

public class Cashier implements SalesAssistant {
    private final OrderNumberSequence orderNumberSequence;
    private final OrderBook orderBook;
    private final ItemInventory inventory;
    private final Cart cart;
    private final Transactor transactor;

    public Cashier(OrderNumberSequence orderNumberSequence, OrderBook orderBook, ItemInventory inventory, Cart cart, Transactor transactor) {
        this.orderNumberSequence = orderNumberSequence;
        this.orderBook = orderBook;
        this.inventory = inventory;
        this.cart = cart;
        this.transactor = transactor;
    }

    public void addToCart(ItemNumber itemNumber) {
        cart.add(inventory.find(itemNumber));
    }

    public BigDecimal orderTotal() {
        return cart.getGrandTotal();
    }

    public Iterable<CartItem> orderContent() {
        return cart.getItems();
    }

    public OrderNumber placeOrder(final PaymentMethod paymentMethod) throws Exception {
        QueryUnitOfWork<OrderNumber> transaction = new QueryUnitOfWork<OrderNumber>() {
            public OrderNumber query() throws Exception {
                OrderNumber nextNumber = orderNumberSequence.nextOrderNumber();
                final Order order = new Order(nextNumber);
                order.addItemsFrom(cart);
                order.pay(paymentMethod);
                orderBook.record(order);
                return nextNumber;
            }
        };
        transactor.perform(transaction);
        cart.clear();
        return transaction.result;
    }
}
