package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;
import com.edgareldy.jsftutorial.entity.Product;
import com.edgareldy.jsftutorial.security.SessionUserHolder;
import com.edgareldy.jsftutorial.service.CustomerService;
import com.edgareldy.jsftutorial.service.OrderService;
import com.edgareldy.jsftutorial.service.ProductService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Reused across three pages with different display modes: {@code place-order.xhtml}
 * (loadProduct/placeOrder, driven by the {@code productId} view parameter),
 * {@code my-orders.xhtml} (getMyOrders) and {@code admin/orders.xhtml}
 * (getAllOrders). Each getter loads its own data lazily on first access, so a
 * single bean serves all three without any page needing to know about the
 * others' state.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@Named
@ViewScoped
public class OrderBean implements Serializable {

    @Inject
    private OrderService orderService;

    @Inject
    private ProductService productService;

    @Inject
    private CustomerService customerService;

    @Inject
    private SessionUserHolder sessionUserHolder;

    private Long productId;
    private Product product;
    private int quantity = 1;
    private List<Order> myOrders;
    private List<Order> allOrders;

    public void loadProduct() {
        if (productId != null) {
            product = productService.findById(productId);
        }
    }

    public String placeOrder() {
        Optional<Customer> customer = customerService.findByUser(sessionUserHolder.getUser());
        if (!customer.isPresent()) {
            FacesMessageUtil.addError("Please complete your profile before placing an order.");
            return "/customer/profile.xhtml?faces-redirect=true";
        }

        Order order = new Order();
        order.setCustomer(customer.get());
        order.setProduct(product);
        order.setQuantity(quantity);
        orderService.create(order);

        FacesMessageUtil.addInfo("Order placed.");
        return "/customer/my-orders.xhtml?faces-redirect=true";
    }

    public List<Order> getMyOrders() {
        if (myOrders == null) {
            myOrders = customerService.findByUser(sessionUserHolder.getUser())
                    .map(orderService::findByCustomer)
                    .orElse(Collections.emptyList());
        }
        return myOrders;
    }

    public List<Order> getAllOrders() {
        if (allOrders == null) {
            allOrders = orderService.findAll();
        }
        return allOrders;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
