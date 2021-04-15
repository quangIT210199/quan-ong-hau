package com.codelovers.quanonghau.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_image")
public class UserImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "type", length = 45, nullable = false)
    private String type;

    @Column(name = "size", length = 64, nullable = false)
    private int size;

    //image bytes can have large lengths so we specify a value
    //which is more than the default length for picByte column
    @Column(name = "pic_byte", length = 1000)
    private byte[] picByte;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true) // phải để unique
    private User user;

    public UserImage( String name, String type, int size, byte[] picByte) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.picByte = picByte;
    }

    public UserImage() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getPicByte() {
        return picByte;
    }

    public void setPicByte(byte[] picByte) {
        this.picByte = picByte;
    }
}
