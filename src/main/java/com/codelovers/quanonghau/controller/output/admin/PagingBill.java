package com.codelovers.quanonghau.controller.output.admin;

import com.codelovers.quanonghau.entity.Bill;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingBill {
    private int currentPage;
    private int totalPages;
    private long startCount;
    private long endCount;
    private long totalItems;
    private String sortDir;
    private String reverseSortDir;
    private List<Bill> billList = new ArrayList<>();
}
