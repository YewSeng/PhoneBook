package com.yewseng.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.yewseng.exception.ContactNotFoundException;
import com.yewseng.model.Contact;
import com.yewseng.response.PagedResponse;
import com.yewseng.service.ContactService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/contacts")
public class ContactController {

	private static final int DEFAULT_PAGE_SIZE = 5;
	
    private static final String NAME_REGEX = "^[a-zA-Z ]{4,}$";
    private static final String PHONE_NUMBER_REGEX = "^[89]\\d{7}$";
    private static final int MIN_ADDRESS_LENGTH = 10;

    private static final String EMPTY_NAME_ERROR = "Name cannot be empty";
    private static final String INVALID_NAME_ERROR = "Name must contain only letters and spaces and be at least 4 characters long";
    private static final String EMPTY_PHONE_NUMBER_ERROR = "Phone Number cannot be empty";
    private static final String INVALID_PHONE_NUMBER_ERROR = "Phone Number must start with 8 or 9 and have 7 consecutive digits";
    private static final String EMPTY_ADDRESS_ERROR = "Address cannot be empty";
    private static final String INVALID_ADDRESS_ERROR = "Address must be at least 10 characters long";
    
	private final ContactService contactService;
	
	@Autowired
	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}
	
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return EMPTY_NAME_ERROR;
        } else if (!name.matches(NAME_REGEX)) {
            return INVALID_NAME_ERROR;
        } else {
            return ""; // No error
        }
    }
    
    // Assuming a Singapore phone number with 8 digits
    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return EMPTY_PHONE_NUMBER_ERROR;
        } else if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            return INVALID_PHONE_NUMBER_ERROR;
        } else {
            return ""; // No error
        }
    }
    
    private String validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return EMPTY_ADDRESS_ERROR;
        } else if (address.trim().length() < MIN_ADDRESS_LENGTH) {
            return INVALID_ADDRESS_ERROR;
        } else {
            return ""; // No error
        }
    }
    
    private String validateContactForm(String name, String phoneNumber, String address) {
        // Validation checks
        StringBuilder errorMessage = new StringBuilder();
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append(EMPTY_NAME_ERROR + "\n");
        } else if (!name.matches(NAME_REGEX)) {
        	errorMessage.append(INVALID_NAME_ERROR + "\n");
        }
        
        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
        	errorMessage.append(EMPTY_PHONE_NUMBER_ERROR + "\n");
        } else if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
        	errorMessage.append(INVALID_PHONE_NUMBER_ERROR + "\n");
        }
        
        // Validate address
        if (address == null || address.trim().isEmpty()) {
        	errorMessage.append(EMPTY_ADDRESS_ERROR + "\n");
        } else if (address.length() < MIN_ADDRESS_LENGTH) {
        	errorMessage.append(INVALID_ADDRESS_ERROR + "\n");
        }
        
        return errorMessage.toString();
    }
    
    private int calculatePageNumberForContact(Contact contact) {
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageNumber = 1; // Start from page 1 (not 0)
        boolean contactFound = false;

        while (!contactFound) {
            // Fetch contacts for the current page number
            PagedResponse<Contact> contactPage = contactService.getAllContacts(pageNumber, pageSize);
            List<Contact> contacts = contactPage.getData();

            // Check if the contact is in the current page
            if (contacts.contains(contact)) {
                contactFound = true;
            } else {
                // If contact not found, increment the page number
                pageNumber++;

                // Break the loop if we have reached the last page
                if (pageNumber > contactPage.getTotalPages()) {
                    pageNumber = 1; // If pageNumber not found return to first page
                    break;
                }
            }
        }

        return pageNumber;
    }

    @GetMapping("/createContact")
	public ModelAndView goToCreateContactPage(HttpServletRequest request, 
			HttpServletResponse response) {
		log.info("Entered into the /createContact request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("createContact");
		log.info("Went to createContact.jsp page");
	    // Get flash attributes and add them to the model
	    Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
	    if (flashMap != null) {
	        mv.addObject("name", flashMap.get("name"));
	        mv.addObject("phoneNumber", flashMap.get("phoneNumber"));
	        mv.addObject("address", flashMap.get("address"));
	    }
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
	}
	
	@PostMapping("/registerContact")
	public ModelAndView createContact(@RequestParam String name,
            @RequestParam String phoneNumber,
            @RequestParam String address,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
		log.info("Entered into the /registerContact request");
		ModelAndView mv = new ModelAndView();
		String nameError = "";
		String phoneNumberError = "";
		String addressError = "";
		int pageNumber = 1;
		try {
			// Perform form validation
			nameError = validateName(name);
			phoneNumberError = validatePhoneNumber(phoneNumber);
			addressError = validateAddress(address);
			
			String errorMessage = validateContactForm(name, phoneNumber, address);
	        if (!errorMessage.isEmpty()) {
	        	throw new IllegalArgumentException(errorMessage);	        
	        }
	        // Create a new Contact object
	        Contact newContact = new Contact(name, phoneNumber, address);
	        Contact createdContact = contactService.createContact(newContact);
	        pageNumber = calculatePageNumberForContact(createdContact);
	        // Redirect with success message and new contact details
	        redirectAttributes.addFlashAttribute("successMessage", 
	        		"Contact created successfully.");
	        redirectAttributes.addFlashAttribute("createdContact", createdContact);

	        // Redirect to a success page or another appropriate view
	        mv.setViewName("redirect:/api/v1/contacts/getAllContacts?page="
	        		+ pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
	        response.setStatus(HttpServletResponse.SC_OK);
	        return mv;
		} catch (IllegalArgumentException e) {
	    	log.error("One or more of the fields are wrong");
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("nameError", nameError); 
	        redirectAttributes.addFlashAttribute("phoneNumberError", phoneNumberError); 
	        redirectAttributes.addFlashAttribute("addressError", addressError);
	        redirectAttributes.addFlashAttribute("name", name);
	        redirectAttributes.addFlashAttribute("phoneNumber", phoneNumber);
	        redirectAttributes.addFlashAttribute("address", address);
	        mv.setViewName("redirect:/api/v1/contacts/createContact");
	        response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
        return mv;
	}
	
	@GetMapping("/editContact/{contactId}")
	public ModelAndView goToUpdateContactPage(@PathVariable UUID contactId, 
			HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /editContact/{contactId} request for "
        		+ "contactId: {}", contactId);
        ModelAndView mv = new ModelAndView();
        try {
        	// Retrieve contact information based on the provided contactId
        	Optional<Contact> optionalContact = contactService.findContactById(contactId);
        	if (optionalContact.isPresent()) {
             	Contact contact = optionalContact.get();
                 // Add the contact object to the ModelAndView
                 mv.addObject("contact", contact);
                 
                 // Set the view name to the JSP page for updating contact
                 mv.setViewName("editContact");
                 response.setStatus(HttpServletResponse.SC_OK);
                 log.info("Went to update Contact page");
            } else {
                 // If contact not found, set view to contactNotFound page
                 mv.setViewName("contactNotFound");
                 mv.addObject("errorMessage", "Contact not found with contactId: " 
                 + contactId);
                 // Set HTTP status to 404 (Not Found)
                 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving "
            		+ "contact with contactId: {}", contactId, e);
            mv.setViewName("generalError");
            mv.addObject("errorMessage", "An error occurred while retrieving contact");
            // Set HTTP status to 500 (Internal Server Error)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mv;
	}
	
	@PutMapping("/updateContact/{contactId}")
    public ModelAndView updateContact(@PathVariable UUID contactId,
            @RequestParam String name,
            @RequestParam String phoneNumber,
            @RequestParam String address,
            HttpServletRequest request, 
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        log.info("Entered into the /updateContact/{ContactId} request for "
        		+ "contactId: {}", contactId);
        ModelAndView mv = new ModelAndView();
		String nameError = "";
		String phoneNumberError = "";
		String addressError = "";
		int pageNumber = 1;
		try {
			// Perform form validation
			nameError = validateName(name);
			phoneNumberError = validatePhoneNumber(phoneNumber);
			addressError = validateAddress(address);
			
			String errorMessage = validateContactForm(name, phoneNumber, address);
	        if (!errorMessage.isEmpty()) {
	        	throw new IllegalArgumentException(errorMessage);	        
	        }
	        
	        Optional<Contact> oldContactOptional = contactService.findContactById(contactId);
	        if (oldContactOptional.isPresent()) {
	        	Contact oldContact = oldContactOptional.get();
	        	redirectAttributes.addFlashAttribute("oldContact", oldContact);
	        }
	        
	        Contact newContactDetails = new Contact(name, phoneNumber, address);
	        Contact newContact = contactService
	        		.updateContact(contactId, newContactDetails);
	        pageNumber = calculatePageNumberForContact(newContact);
	        redirectAttributes.addFlashAttribute("pageNumber", pageNumber);
			redirectAttributes.addFlashAttribute("updatedContact", newContact);
	        // Set success message and redirect to getAllContacts
	        redirectAttributes.addFlashAttribute("successMessage", 
	        		"Contact with contactId " + contactId + " is successfully updated."); 
	        // Redirect to filtered view
	        mv.setViewName("redirect:/api/v1/contacts/getAllContacts?page=" 
	        + pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
	        response.setStatus(HttpServletResponse.SC_OK);        
		} catch(IllegalArgumentException | ContactNotFoundException e) {
	    	log.error("One or more of the fields are wrong");
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("nameError", nameError); 
	        redirectAttributes.addFlashAttribute("phoneNumberError", phoneNumberError); 
	        redirectAttributes.addFlashAttribute("addressError", addressError);
	        redirectAttributes.addFlashAttribute("name", name);
	        redirectAttributes.addFlashAttribute("phoneNumber", phoneNumber);
	        redirectAttributes.addFlashAttribute("address", address);
            mv.setViewName("redirect:/api/v1/contacts/editContact/{contactId}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
        return mv;
	}
	
	@DeleteMapping("/deleteContact/{contactId}")
    public ModelAndView deleteContact(@PathVariable UUID contactId,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
		log.info("Entered into the /deleteContact/{contactId} request "
				+ "for contactId: {}", contactId);
		ModelAndView mv = new ModelAndView();
		try {
			// Retrieve the contact data before performing the delete operation
			Optional<Contact> deletedContactOptional = contactService
					.findContactById(contactId);
			
			if (deletedContactOptional.isPresent()) {
				Contact deletedContact = deletedContactOptional.get();
				
				// Perform the delete operation
				contactService.deleteContact(contactId);
			    log.info("Contact with contactId: {} has been "
			    		+ "successfully deleted", contactId);
			    redirectAttributes.addFlashAttribute("successMessage", 
			    		"Contact deleted successfully.");
			    redirectAttributes.addFlashAttribute("deletedContact", deletedContact);
			}
		} catch(ContactNotFoundException e) {
            log.error("Contact not found while deleting contact "
            		+ "with contactId: {}", contactId, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
            		"Contact not found with contactId: " + contactId);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
		} catch(Exception e) {
            log.error("Error occurred while deleting contact "
            		+ "with contactId: {}", contactId, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
            		"An error occurred while deleting contact: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
		}
		// Redirect to the referring page
	    mv.setViewName("redirect:" + request.getHeader("referer"));
		return mv;
	}

    @GetMapping("/getAllContacts")
    public ModelAndView getAllContacts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpServletRequest request, 
            HttpServletResponse response) {
        
    	log.info("Entered into the /getAllContacts request");
    	
        ModelAndView mv = new ModelAndView("viewContacts");
        PagedResponse<Contact> pagedResponse = contactService
        		.getAllContacts(page, size);
        
        mv.addObject("contacts", pagedResponse.getData());
        mv.addObject("totalCount", pagedResponse.getTotalCount());
        mv.addObject("currentPage", pagedResponse.getCurrentPage());
        mv.addObject("pageSize", pagedResponse.getPageSize());
        mv.addObject("totalPages", pagedResponse.getTotalPages());
        mv.addObject("hasNextPage", pagedResponse.isHasNextPage());
        mv.addObject("hasPreviousPage", pagedResponse.isHasPreviousPage());
        
        return mv;
    }
    
    @GetMapping("/filterContacts")
    public ModelAndView filterContacts(
            @RequestParam Map<String, Object> searchCriteria,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        ModelAndView mv = new ModelAndView("viewContacts");
        PagedResponse<Contact> pagedResponse = contactService
        		.filterContacts(searchCriteria, page, size);
        
        mv.addObject("contacts", pagedResponse.getData());
        mv.addObject("totalCount", pagedResponse.getTotalCount());
        mv.addObject("currentPage", pagedResponse.getCurrentPage());
        mv.addObject("pageSize", pagedResponse.getPageSize());
        mv.addObject("totalPages", pagedResponse.getTotalPages());
        mv.addObject("hasNextPage", pagedResponse.isHasNextPage());
        mv.addObject("hasPreviousPage", pagedResponse.isHasPreviousPage());
        mv.addObject("searchCriteria", searchCriteria);
        
        return mv;
    }
}
