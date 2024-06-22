package com.yewseng.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.yewseng.utils.contract.QueryBuilder;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public abstract class QueryBuilderImplementation<T> implements QueryBuilder<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<T> criteriaQuery;
    private final Root<T> root;
    private final List<Predicate> predicates;
    private final List<Order> orders;

    @Autowired
    public QueryBuilderImplementation(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(clazz);
        this.root = criteriaQuery.from(clazz);
        this.predicates = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    @Override
    public QueryBuilder<T> where(Specification<T> specification) {
        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        if (predicate != null) {
            predicates.add(predicate);
        }
        return this;
    }

    @Override
    public <K extends Comparable<? super K>> QueryBuilder<T> orderBy(Function<Root<T>, Expression<?>> keySelector) {
        orders.add(criteriaBuilder.asc(keySelector.apply(root)));
        return this;
    }

    @Override
    public <K extends Comparable<? super K>> QueryBuilder<T> orderByDescending(Function<Root<T>, Expression<?>> keySelector) {
        orders.add(criteriaBuilder.desc(keySelector.apply(root)));
        return this;
    }

    @Override
    public List<T> build() {
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(orders);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}