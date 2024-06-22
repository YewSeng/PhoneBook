package com.yewseng.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.yewseng.exception.ContactNotFoundException;
import com.yewseng.model.Contact;
import com.yewseng.repository.ContactRepository;
import com.yewseng.response.PagedResponse;
import com.yewseng.utils.ContactPage;

@SpringBootTest 
@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

	@MockBean
    private ContactRepository contactRepository;
    
    @Mock
    private ContactPage contactPage;
    
    @Autowired
    private ContactService contactService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Test findContactById method")
    public void testFindContactById() {
        // Arrange
        UUID contactId = UUID.randomUUID(); 
        Contact mockContact = new Contact();
        mockContact.setContactId(contactId);

        // Mocking behavior of the repository
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(mockContact));

        // Act
        Optional<Contact> foundContact = contactService.findContactById(contactId);

        // Assert
        assertTrue(foundContact.isPresent()); 
        assertEquals(contactId, foundContact.get().getContactId()); 
    }
    
    @Test
    @DisplayName("Test createContact method")
    public void testCreateContact() {
        // Arrange
        Contact inputContact = new Contact("John Doe", "1234567890", "123 Main St");

        // Mock behavior of the repository
        UUID generatedUUID = UUID.randomUUID(); 
        when(contactRepository.save(inputContact)).thenAnswer(invocation -> {
            Contact savedContact = invocation.getArgument(0);
            savedContact.setContactId(generatedUUID); 
            return savedContact;
        });

        // Act
        Contact createdContact = contactService.createContact(inputContact);

        // Assert
        assertNotNull(createdContact); 
        assertNotNull(createdContact.getContactId());
        assertEquals(inputContact.getName(), createdContact.getName()); 
        assertEquals(inputContact.getPhoneNumber(), createdContact.getPhoneNumber());
        assertEquals(inputContact.getAddress(), createdContact.getAddress());
        assertEquals(generatedUUID, createdContact.getContactId()); 
        verify(contactRepository, times(1)).save(inputContact); 
    }
    
    @Test
    @DisplayName("Test updateContact method - existing contact")
    public void testUpdateContactExistingContact() throws ContactNotFoundException {
        // Arrange
        UUID contactId = UUID.randomUUID();
        Contact existingContact = new Contact(contactId, "John Doe", "1234567890", "123 Main St");
        Contact updatedContactDetails = new Contact(null, "Jane Doe", "9876543210", "456 Elm St");

        // Mock behavior of the repository
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(existingContact); 

        // Act
        Contact updatedContact = contactService.updateContact(contactId, updatedContactDetails);

        // Assert
        assertNotNull(updatedContact); 
        assertEquals(contactId, updatedContact.getContactId()); 
        assertEquals(updatedContactDetails.getName(), updatedContact.getName()); 
        assertEquals(updatedContactDetails.getPhoneNumber(), updatedContact.getPhoneNumber());
        assertEquals(updatedContactDetails.getAddress(), updatedContact.getAddress());
        verify(contactRepository, times(1)).findById(contactId); 
        verify(contactRepository, times(1)).save(existingContact); 
    }

    @Test
    @DisplayName("Test updateContact method - non-existing contact")
    public void testUpdateContactNonExistingContact() {
        // Arrange
        UUID nonExistingContactId = UUID.randomUUID();
        Contact updatedContactDetails = new Contact(null, "Jane Doe", "9876543210", "456 Elm St");

        // Mock behavior of the repository
        when(contactRepository.findById(nonExistingContactId)).thenReturn(Optional.empty());

        // Act and Assert
        ContactNotFoundException exception = assertThrows(ContactNotFoundException.class, () -> {
            contactService.updateContact(nonExistingContactId, updatedContactDetails);
        });
        assertEquals("Contact not found with contactId: " + nonExistingContactId, exception.getMessage());
        verify(contactRepository, times(1)).findById(nonExistingContactId);
    }

    @Test
    @DisplayName("Test deleteContact method - existing contact")
    public void testDeleteContactExistingContact() throws ContactNotFoundException {
        // Arrange
        UUID contactId = UUID.randomUUID();
        Contact existingContact = new Contact(contactId, "John Doe", "1234567890", "123 Main St");

        // Mock behavior of the repository
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));

        // Act
        contactService.deleteContact(contactId);

        // Assert
        verify(contactRepository, times(1)).findById(contactId); 
        verify(contactRepository, times(1)).deleteById(contactId); 
    }

    @Test
    @DisplayName("Test deleteContact method - non-existing contact")
    public void testDeleteContactNonExistingContact() {
        // Arrange
        UUID nonExistingContactId = UUID.randomUUID();

        // Mock behavior of the repository
        when(contactRepository.findById(nonExistingContactId)).thenReturn(Optional.empty());

        // Act and Assert
        ContactNotFoundException exception = assertThrows(ContactNotFoundException.class, () -> {
            contactService.deleteContact(nonExistingContactId);
        });
        assertEquals("Contact not found with contactId: " + nonExistingContactId, exception.getMessage());
        verify(contactRepository, times(1)).findById(nonExistingContactId); 
    }
    
    @Test
    @DisplayName("Test getAllContacts method")
    public void testGetAllContacts() {
        // Arrange
        int page = 1;
        int pageSize = 10;
        List<Contact> mockContacts = Arrays.asList(
                new Contact(UUID.randomUUID(), "John Doe", "1234567890", "123 Main St"),
                new Contact(UUID.randomUUID(), "Jane Smith", "9876543210", "456 Elm St")
        );

        // Mock the ContactService to return the mockContacts
        ContactService mockedContactService = mock(ContactService.class);
        when(mockedContactService.getAllContacts(page, pageSize)).thenReturn(new PagedResponse<>(mockContacts, mockContacts.size(), page, pageSize));

        // Act
        PagedResponse<Contact> result = mockedContactService.getAllContacts(page, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(mockContacts.size(), result.getData().size(), "Incorrect number of contacts returned");
        assertEquals(mockContacts.size(), result.getTotalCount(), "Incorrect total count of contacts");
        assertEquals(page, result.getCurrentPage(), "Incorrect page number");
        assertEquals(pageSize, result.getPageSize(), "Incorrect page size");
    }
    
    @Test
    @DisplayName("Test filterContacts method with single field criteria")
    public void testFilterContactsSingleFieldCriteria() {
        // Arrange
        int page = 1;
        int pageSize = 10;
        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put("name", "John Doe");
        List<Contact> filteredMockContacts = Arrays.asList(
                new Contact(UUID.randomUUID(), "John Doe", "1234567890", "123 Main St")
        );
        
        // Mock the ContactService to return the mockContacts
        ContactService mockedContactService = mock(ContactService.class);
        when(mockedContactService.filterContacts(searchCriteria, page, pageSize))
        .thenReturn(new PagedResponse<>(filteredMockContacts, filteredMockContacts.size(), page, pageSize));

        // Act
        PagedResponse<Contact> result = mockedContactService.filterContacts(searchCriteria, page, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(filteredMockContacts.size(), result.getData().size(), "Incorrect number of filtered contacts returned");
        assertEquals(filteredMockContacts.size(), result.getTotalCount(), "Incorrect total count of filtered contacts");
        assertEquals(page, result.getCurrentPage(), "Incorrect page number");
        assertEquals(pageSize, result.getPageSize(), "Incorrect page size");
    }

    @Test
    @DisplayName("Test filterContacts method with multiple fields criteria")
    public void testFilterContactsMultipleFieldsCriteria() {
        // Arrange
        int page = 1;
        int pageSize = 10;
        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put("name", "John Doe");
        searchCriteria.put("phoneNumber", "1234567890");
        List<Contact> filteredMockContacts = Arrays.asList(
                new Contact(UUID.randomUUID(), "John Doe", "1234567890", "123 Main St")
        );
        
        // Mock the ContactService to return the mockContacts
        ContactService mockedContactService = mock(ContactService.class);
        when(mockedContactService.filterContacts(searchCriteria, page, pageSize))
        .thenReturn(new PagedResponse<>(filteredMockContacts, filteredMockContacts.size(), page, pageSize));

        // Act
        PagedResponse<Contact> result = mockedContactService.filterContacts(searchCriteria, page, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(filteredMockContacts.size(), result.getData().size(), "Incorrect number of filtered contacts returned");
        assertEquals(filteredMockContacts.size(), result.getTotalCount(), "Incorrect total count of filtered contacts");
        assertEquals(page, result.getCurrentPage(), "Incorrect page number");
        assertEquals(pageSize, result.getPageSize(), "Incorrect page size");
    }
}

