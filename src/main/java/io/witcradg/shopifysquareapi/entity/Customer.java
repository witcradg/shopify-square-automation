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

	@JsonProperty("idempotency_key")
	private String idempotencyKey;
	
	@JsonProperty("phone_number")
	private String phoneNumber;
}
