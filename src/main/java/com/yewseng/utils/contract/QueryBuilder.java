package com.yewseng.utils.contract;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

public interface QueryBuilder<T> {
    QueryBuilder<T> where(Specification<T> specification);
    <K extends Comparable<? super K>> QueryBuilder<T> orderBy(Function<Root<T>, Expression<?>> keySelector);
    <K extends Comparable<? super K>> QueryBuilder<T> orderByDescending(Function<Root<T>, Expression<?>> keySelector);
    List<T> build();
}
