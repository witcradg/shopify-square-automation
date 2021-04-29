package io.witcradg.shopifysquareapi.entity;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CustomerOrder {
	
	public CustomerOrder(JSONObject content) throws Exception {
		
		System.out.println("Customer constructor" + content.getJSONObject("user"));

		this.setEmailAddress(content.getJSONObject("user").getString("email"));
		this.setInvoiceNumber(content.getString("invoiceNumber"));
		this.setInvoiceTotal(content.getInt("finalGrandTotal") );
		this.setOrderWeight(content.getInt("totalWeight"));


		JSONObject address = content.getJSONObject("shippingAddress");

		this.setPhoneNumber(address.getString("phone"));

		this.setCompanyName(address.getString("company"));
		this.setFamilyName(address.getString("name"));
		this.setGivenName(address.getString("firstName"));
		this.setAddressLine1(address.getString("address1"));
		this.setAddressLine2(address.getString("address2"));
		this.setCity(address.getString("city"));
		this.setState(address.getString("province"));
		this.setPostalCode(address.getString("postalCode"));
	}

	private String companyName;
	private String emailAddress;
	private String familyName;
	private String givenName;
	private String nickname;
	private String phoneNumber;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String state;
	private String postalCode;

	private String invoiceNumber;
	private Integer invoiceTotal;
	private Integer orderWeight;
}
