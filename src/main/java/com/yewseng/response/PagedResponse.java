package com.yewseng.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

	private List<T> data;
    private int totalCount;  
    private int currentCount;  
    private int pageSize;
    private int currentPage;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;

    public PagedResponse(List<T> data, int totalCount, int currentPage, int pageSize) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentCount = data.size();
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        this.hasNextPage = currentPage < totalPages;
        this.hasPreviousPage = currentPage > 1;
    }
}
