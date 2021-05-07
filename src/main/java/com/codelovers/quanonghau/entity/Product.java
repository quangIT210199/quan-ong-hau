package com.codelovers.quanonghau.entity;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "products")
public class Product implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 256, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 256, nullable = false, unique = true)
    private String alias; // Bí danh

    @Column(name = "full_description", length = 4096, nullable = false)
    private String fullDescription;

    @Column(name = "short_description", length = 1024, nullable = false)
    private String shortDescription;

    @Column(name = "in_stock")
    private boolean inStock;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "updated_time")
    private String updatedTime;

    private boolean enabled;

    @Column(name = "price")
    private float price;

    @Column(name = "cost")
    private float cost;

    @Column(name = "discount_percent")
    private float discountPercent;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    // orphanRemoval: Mối đối tượng company sẽ chứa 1 tập hợp các đối tượng employee, khi một đối tượng employee bị xóa khỏi tập hợp đó thì nó sẽ bị xóa khỏi database.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetails> details = new ArrayList<>(); // Sản phẩm cần tham chiếu tới các Chi tiết nên cần sử dụng

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // tham chiếu khóa ngoại

    public Product() {
    }

    public void addDetails(String name, String value) {
        this.details.add(new ProductDetails(name, value, this));
    }

    // Extra Image
    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null ) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/default-user.png").toUriString();
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/" + this.id + "/" + this.mainImage).toUriString();
    }
    ///

    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Set<ProductImage> getImages() {
        return images;
    }

    public void setImages(Set<ProductImage> images) {
        this.images = images;
    }

    public List<ProductDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ProductDetails> details) {
        this.details = details;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }
}
