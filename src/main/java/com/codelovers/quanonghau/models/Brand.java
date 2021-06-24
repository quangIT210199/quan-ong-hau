package com.codelovers.quanonghau.models;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name",nullable = false, length = 45, unique = true)
    private String name;

    @Column(name = "logo", nullable = false, length = 128)
    private String logo;

    @ManyToMany // This call Unidirectional Many to Many
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
            )
    private Set<Category> categories = new HashSet<>();

    public Brand(String name) {
        this.name = name;
        this.logo = "brand-logo.png";
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand() {
    }

    @Transient
    public String getPathImageLoge() {
        if (logo == null || id == null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/brand-photo/default-brand.png").toUriString();
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/brand-photo/" + this.id + "/" + this.logo).toUriString();
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}
