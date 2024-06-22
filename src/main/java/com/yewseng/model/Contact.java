package com.yewseng.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Tbl_Contact")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Contact {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false)
	private UUID contactId;	
    
    @Column(name = "contactName", length = 50, nullable = false)
	private String name;
	
	@Column(name = "mobileNumber", length = 8, nullable = false)
	private String phoneNumber;
	
	@Column(name = "contactAddress", length = 255, nullable = false)
	private String address;
	
	public Contact(String name, String phoneNumber, String address) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
	
    public String toString() { 
        return "[Contact] " + "(" + name + ", " + phoneNumber + ", " + address + ")";
    } 
}
