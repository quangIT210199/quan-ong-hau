package com.codelovers.quanonghau.help;

import lombok.Data;

@Data
public class PageInfoCategory {
    // Because API /category/categories/page return struct category Not return page
    // Then when used PageInfoCategory for save info of Page
    private int totalPages;
    private long totalElements;
}
