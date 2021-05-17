package com.codelovers.quanonghau.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorys")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

//    @OneToMany(mappedBy = "category", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Product> products = new ArrayList<>(); // k cần tham chiếu tới bất kì sản phẩm nào -> không dùng

    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 64, nullable = false, unique = true)
    private String alias;

    @Column(name = "image", length = 128, nullable = false)
    private String image;

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs; // need field for store Parent Id with format -1- or -1-5-

    private boolean enabled;

    // Tu tham chieu de phan cap thu bac danh muc
    // https://stackoverflow.com/questions/2302802/how-to-fix-the-hibernate-object-references-an-unsaved-transient-instance-save
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    @OrderBy("name asc") // This annonition just work with relationship OneToMany and ManyToMany
    private Set<Category> children = new HashSet<>();

    @Transient
    private String getImagePath() {
        if (id == null || image == null)
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/category-photo/default-user.png").toUriString();

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("images/category-photo/" + this.id + "/" + this.image).toUriString();
    }

    @Transient
    private boolean hasChildren;

    // Tạo subCate
    public static Category copyIdAndName(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());

        return copyCategory;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category copyCategory = new Category();
        copyCategory.setId(id);
        copyCategory.setName(name);

        return copyCategory;
    }

    public static Category copyFull(Category category) { // Using when save category not have --
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setImage(category.getImage());

        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }

    //Tạo sub cate
    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    // Tạo subCate
    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(Integer id, String name, String alias) {// For Test
        super();
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(Integer id) {
        this.id = id;
    }

    public Category() {
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getAllParentIDs() {
        return allParentIDs;
    }

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }
}
