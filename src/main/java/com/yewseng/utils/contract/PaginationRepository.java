package com.yewseng.utils.contract;

import java.util.Map;

public interface PaginationRepository<T> {
    Iterable<T> getPage(int page, int pageSize);
    PageData<T> getPageBySearchTypeAndSearchTerm(int page, int pageSize, 
    		Map<String, Object> searchCriteria);
    int getTotalCount();
    int getTotalPages(int pageSize);
    boolean hasNextPage(int page, int pageSize);
    boolean hasPreviousPage(int page);
}
