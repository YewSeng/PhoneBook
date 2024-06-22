package com.yewseng.utils.contract;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.Data;

@Data
public class PageData<T> {
    private final Iterable<T> data;
    private final int totalCount;

    @Autowired
    public PageData(Iterable<T> data, int totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }
}
