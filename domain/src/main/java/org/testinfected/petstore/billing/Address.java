package org.testinfected.petstore.billing;

import org.testinfected.petstore.validation.Constraint;
import org.testinfected.petstore.validation.NotEmpty;
import org.testinfected.petstore.validation.Validates;
import org.testinfected.petstore.validation.NotNull;

import java.io.Serializable;

public class Address implements Serializable {
    private final NotNull<String> firstName;
    private final NotNull<String> lastName;
    private final String emailAddress;

    private final String zipCode;
	private final NotEmpty country;

	public Address(String firstName, String lastName, String emailAddress, String zipCode, String country) {
		this.firstName = Validates.notNull(firstName);
        this.lastName = Validates.notNull(lastName);
        this.emailAddress = emailAddress;
        this.zipCode = zipCode;
        this.country = Validates.notEmpty(country);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
    	return country.get();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (!firstName.equals(address.firstName)) return false;
        if (!lastName.equals(address.lastName)) return false;
        if (zipCode == null && address.zipCode != null || zipCode != null && !zipCode.equals(address.zipCode)) return false;
        if (!country.equals(address.country)) return false;
        if (emailAddress != null ? !emailAddress.equals(address.emailAddress) : address.emailAddress != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
