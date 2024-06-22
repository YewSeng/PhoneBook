package com.yewseng.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yewseng.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

}
