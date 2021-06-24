package com.codelovers.quanonghau.configs.payload;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("tokenType")
    private String tokenType = "Bearer";

    @SerializedName("id")
    private Integer id;

    @SerializedName("username")
    private String username;

    @SerializedName("roles")
    private List<String> roles;

    public LoginResponse(String accessToken, Integer id, String username, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getId() {
        return id;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
