package com.realestate.realestate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sellerId;

    private String sellerName;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type")
    private SellerType sellerType;   // OWNER or AGENT

    public int getSellerId() {
        return sellerId;
    }
    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public SellerType getSellerType() {
        return sellerType;
    }
    public void setSellerType(SellerType sellerType) {
        this.sellerType = sellerType;
    }
}
