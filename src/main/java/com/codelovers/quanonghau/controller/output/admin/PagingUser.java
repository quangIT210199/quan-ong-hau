package com.codelovers.quanonghau.controller.output.admin;

import com.codelovers.quanonghau.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingUser {
    private int currentPage;
    private int totalPage;
    private long startCount;
    private long endCount;
    private long totalItems;
    private String sortField;
    private String sortDir;
    private String keyword;
    private String reverseSortDir;
    private List<User> userList = new ArrayList<>();
}
