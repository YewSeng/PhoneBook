package com.yewseng.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yewseng.exception.ContactNotFoundException;
import com.yewseng.model.Contact;
import com.yewseng.repository.ContactRepository;
import com.yewseng.response.PagedResponse;
import com.yewseng.utils.ContactPage;
import com.yewseng.utils.contract.PageData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContactService {

	private final ContactRepository contactRepository;
	private final ContactPage contactPage;
	
	@Autowired
	public ContactService(ContactRepository contactRepository, 
			ContactPage contactPage) {
		this.contactRepository = contactRepository;
		this.contactPage = contactPage;
	}
	
	public Optional<Contact> findContactById(UUID contactId) {
		log.info("Finding contact with contactId: {}", contactId);
		return contactRepository.findById(contactId);
	}
	
	public Contact createContact(Contact contact) {
		UUID contactId = UUID.randomUUID();
		contact.setContactId(contactId);
		log.info("Creating contact with contactId: {}, name: {}, "
				+ "phone number: {}, address: {}", contact.getContactId(),
				contact.getName(), contact.getPhoneNumber(), contact.getAddress());
		return contactRepository.save(contact);
	}
	
	@Transactional
	public Contact updateContact(UUID contactId, Contact newContactDetails) 
			throws ContactNotFoundException {
		log.info("Attempting to find Contact with UUID: {}", contactId);
		Optional<Contact> optionalContact = contactRepository.findById(contactId);
		if (optionalContact.isPresent()) {
			log.info("Contact with contactId: {} is found", contactId);
			// if present get the contacts
			Contact oldContact = optionalContact.get();
			// update the contact details wrt newContactDetails
			log.info("Attempting to update Contact details: name = [old] {} [new] {},"
					+ " phone number = [old] {} [new] {}, address = [old] {} [new] {}",
					oldContact.getName(), newContactDetails.getName(), 
					oldContact.getPhoneNumber(), newContactDetails.getPhoneNumber(),
					oldContact.getAddress(), newContactDetails.getAddress());
			oldContact.setName(newContactDetails.getName());
			oldContact.setPhoneNumber(newContactDetails.getPhoneNumber());
			oldContact.setAddress(newContactDetails.getAddress());
			Contact updatedContact = contactRepository.save(oldContact);
			log.info("Update is successful for contact with contactId: {}", contactId);
			return updatedContact;
		} else {
			log.error("Contact not found with contactId: {}", contactId);
			throw new ContactNotFoundException("Contact not found with contactId: " 
					+ contactId);
		}
	}
	
	public void deleteContact(UUID contactId) throws ContactNotFoundException {
		log.info("Attempting to delete Contact with UUID: {}", contactId);
		Optional<Contact> optionalContact = contactRepository.findById(contactId);
		if (optionalContact.isPresent()) {
			log.info("Contact with contactId: {} is found", contactId);
			// delete contact
			log.info("Attempting to delete contact with contactId: {}", contactId);
			contactRepository.deleteById(contactId);
			log.info("Delete is successful for contact with contactId: {}", contactId);
		} else {
			log.error("Contact not found with contactId: {}", contactId);
			throw new ContactNotFoundException("Contact not found with contactId: " 
					+ contactId);
		}
	}
	
	public PagedResponse<Contact> getAllContacts(int page, int pageSize) {
		Iterable<Contact> allContacts = contactPage.getPage(page, pageSize);
		int totalCount = contactPage.getTotalCount();
		return new PagedResponse<>(toList(allContacts), totalCount, page, pageSize);
	}
	
    public PagedResponse<Contact> filterContacts(Map<String, Object> searchCriteria, int page, int pageSize) {
        PageData<Contact> filteredContacts = contactPage
        		.getPageBySearchTypeAndSearchTerm(page, pageSize, searchCriteria);
        return new PagedResponse<>(toList(filteredContacts.getData()), 
        		filteredContacts.getTotalCount(), page, pageSize);
    }
	
    // Utility method to convert Iterable to List
    private List<Contact> toList(Iterable<Contact> iterable) {
        List<Contact> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
