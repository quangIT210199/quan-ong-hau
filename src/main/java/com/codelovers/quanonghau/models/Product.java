package com.codelovers.quanonghau.models;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "products")
public class Product implements Serializable {

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
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "price")
    private float price;

    @Column(name = "cost")
    private float cost;

    @Column(name = "discount_percent")
    private float discountPercent;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Column(name = "qr_code_image")
    private String qrCodeImage;

    // orphanRemoval: là một đặc tả trong ORM. Nó đánh dấu rằng các phần tử con sẽ bị xóa khi bạn xóa nó khỏi collection của phần tử cha.
    // CascadeType.ALL: bất cứ sự thay đổi với parent(Product) sẽ update chile(ProductImage)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetails> details = new ArrayList<>(); // Sản phẩm cần tham chiếu tới các Chi tiết nên cần sử dụng

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)// nghi vấn
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToOne // unidirectional : tham chiếu 1 chiều
    @JoinColumn(name = "category_id")
    private Category category; // tham chiếu khóa ngoại

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Product() {
    }

    public void addDetails(String name, String value) {
        this.details.add(new ProductDetails(name, value, this));
    }

    public void addDetails(Integer id, String name, String value) {
        this.details.add((new ProductDetails(id, name, value, this)));
    }

    // Extra Image to Set Collection
    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    // Check the ExtraImage in Set Collection
    public boolean containsImageName(String imageName) {
        Iterator<ProductImage> iterator = images.iterator();

        while (iterator.hasNext()) {
            ProductImage image = iterator.next();

            if (image.getName().equals(imageName)) {
                return true;
            }
        }

        return false;
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/default-user.png").toUriString();
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/" + this.id + "/" + this.mainImage).toUriString();
    }

    @Transient
    public String getQrCodeImagePath(){
        if (id == null || qrCodeImage == null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/default-user.png").toUriString();
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/product-photo/" + this.id + "/qrcode/" + this.qrCodeImage).toUriString();
    }

    @Transient
    public String getShortName() {
        if (name == null) {
            return "";
        }
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }

        return name;
    }

    @Transient
    public double getDiscountPrice() {
        if (discountPercent > 0) {
            return price *((100 - discountPercent) / 100);
        }

        return this.price;
    }
    ///
    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
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

//    public List<CartItem> getCartItems() {
//        return cartItems;
//    }
//
//    public void setCartItems(List<CartItem> cartItems) {
//        this.cartItems = cartItems;
//    }

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
