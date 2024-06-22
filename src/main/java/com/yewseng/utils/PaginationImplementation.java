package com.yewseng.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yewseng.utils.contract.PageData;
import com.yewseng.utils.contract.PaginationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class PaginationImplementation<T> implements PaginationRepository<T> {

    private final EntityManager entityManager;
    private final Class<T> classType;
    private final String defaultSortProperty;

    @Autowired
    public PaginationImplementation(EntityManager entityManager, Class<T> classType, 
                                    String defaultSortProperty) {
        this.entityManager = entityManager;
        this.classType = classType;
        this.defaultSortProperty = defaultSortProperty;
    }

    @Override
    public Iterable<T> getPage(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classType);
        Root<T> root = cq.from(classType);

        log.debug("Building query for getPage method");

        // Order by default sort property in ascending order
        cq.orderBy(cb.asc(root.get(defaultSortProperty)));

        List<T> resultList = entityManager.createQuery(cq)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        log.debug("Executed getPage query successfully");

        return resultList;
    }

    @Override
    public PageData<T> getPageBySearchTypeAndSearchTerm(int page, int pageSize,
    		Map<String, Object> searchCriteria) {
        // No valid searchCriteria provided
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            log.debug("No search criteria provided, using getPage method directly");

            List<T> resultList = (List<T>) getPage(page, pageSize);
            int totalCount = getTotalCount();
            return new PageData<>(resultList, totalCount);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classType);
        Root<T> root = cq.from(classType);

        log.debug("Building query for getPageBySearchTypeAndSearchTerm method");

        List<Predicate> predicates = applySearchCriteria(cb, root, searchCriteria);
        cq.where(predicates.toArray(new Predicate[0]));

        // Order by default sort property in ascending order
        cq.orderBy(cb.asc(root.get(defaultSortProperty)));

        List<T> resultList = entityManager.createQuery(cq)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        int totalCount = getTotalCount(searchCriteria);

        log.debug("Executed getPageBySearchTypeAndSearchTerm query successfully");

        return new PageData<>(resultList, totalCount);
    }

    @Override
    public int getTotalCount() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(classType);
        cq.select(cb.count(root));

        log.debug("Building query for getTotalCount method");

        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public int getTotalCount(Map<String, Object> searchCriteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(classType);

        log.debug("Building query for getTotalCount with searchCriteria method");

        List<Predicate> predicates = applySearchCriteria(cb, root, searchCriteria);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.select(cb.count(root));

        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    @Override
    public int getTotalPages(int pageSize) {
        log.debug("Calculating total pages for pageSize {}", pageSize);

        int totalCount = getTotalCount();
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    @Override
    public boolean hasNextPage(int page, int pageSize) {
        log.debug("Checking if there is a next page for page {} and pageSize {}", 
        		page, pageSize);

        return page < getTotalPages(pageSize);
    }

    @Override
    public boolean hasPreviousPage(int page) {
        log.debug("Checking if there is a previous page for page {}", page);

        return page > 1;
    }

    private List<Predicate> applySearchCriteria(CriteriaBuilder cb,
            Root<T> root, Map<String, Object> searchCriteria) {
        List<Predicate> predicates = new ArrayList<>();

        searchCriteria.forEach((key, value) -> {
            // Find the path case-insensitively
            Path<Object> path = null;
            for (SingularAttribute<T, ?> p : root.getModel().getDeclaredSingularAttributes()) {
                if (p.getName().equalsIgnoreCase(key)) {
                    path = root.get(p.getName());
                    break;
                }
            }

            if (path == null) {
                log.warn("No matching attribute found for key '{}'", key);
                return; // Skip if no matching path found
            }

            if (value instanceof String) {
                // Special handling for phoneNumber and contactId attributes
                if ("phoneNumber".equalsIgnoreCase(key) || "contactId".equalsIgnoreCase(key)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                } else {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                }
            } else if (value instanceof Number) {
                predicates.add(cb.equal(path, value));
            } else if (value instanceof List) {
                List<?> values = (List<?>) value;
                if (!values.isEmpty()) {
                    if (values.get(0) instanceof String) {
                        predicates.add(path.in(values.stream()
                                .map(String::valueOf)
                                .map(String::toLowerCase)
                                .collect(Collectors.toList())));
                    } else {
                        predicates.add(path.in(values));
                    }
                }
            } else if (value instanceof UUID) {
                predicates.add(cb.equal(path, value));
            }
        });

        return predicates;
    }
}
