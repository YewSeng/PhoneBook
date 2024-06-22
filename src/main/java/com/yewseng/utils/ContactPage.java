package com.yewseng.utils;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.yewseng.model.Contact;
import lombok.Data;

@Data
@Component
public class ContactPage extends PaginationImplementation<Contact> {

    @Autowired
    public ContactPage(EntityManager entityManager,
                       @Value("${pagination.defaultSortProperty.contact}") String defaultSortProperty) {
    	super(entityManager, Contact.class, defaultSortProperty);
    }
}
