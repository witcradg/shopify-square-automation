package io.witcradg.shopifysquareapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class Customer {

	@JsonProperty("company_name")
	private String companyName;
	
	@JsonProperty("email_address")
	private String emailAddress;
	
	@JsonProperty("family_name")
	private String familyName;
	
	@JsonProperty("given_name")
	private String givenName;
	
	@JsonProperty("nickname")
	private String nickname;
	
	@JsonProperty("phone_number")
	private String phoneNumber;
	
	@JsonProperty("address_line_1")
	private String addressLine1;

	@JsonProperty("address_line_2")
	private String addressLine2;

	@JsonProperty("address_line_3")
	private String addressLine3;
	
	@JsonProperty("order_number")
	private String orderNumber;
	
	@JsonProperty("order_total")
	private String orderTotal;
}
