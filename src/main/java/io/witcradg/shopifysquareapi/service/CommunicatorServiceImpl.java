package io.witcradg.shopifysquareapi.service;

import java.net.http.HttpResponse;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.witcradg.shopifysquareapi.entity.Customer;
import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

@Service
public class CommunicatorServiceImpl implements ICommunicatorService {
	
	RestTemplate restTemplate = new RestTemplate();
	final private static String CUSTOMER_URL = "https://connect.squareupsandbox.com/v2/customers";

	@Override
	public void createCustomer(Customer customer) {


		/*********************** artificially override the submitted values for testing */
		customer.setFamilyName("Witt");
		customer.setGivenName("Dean");
	    customer.setNickname("Naed");		
	    customer.setIdempotencyKey(UUID.randomUUID().toString());
	    customer.setPhoneNumber("(916) 123-4567");

		System.out.println("running createCustomer stub: " + customer.toString() + " \n\n");		
		
		HttpHeaders headers = new HttpHeaders();

		headers.add("Square-Version", "2021-04-21");
	    headers.add("Authorization", "Bearer EAAAEDtqOqiRL-XbBm_RjAG4Nns-CBJbpmC4CmeIzO-35KQ-LyUymZYzvlg7dJh1"); //TODO token
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    JSONObject jsonObject = new JSONObject();
	    jsonObject.put("company_name", "WitcraftIO");
		jsonObject.put("email_address", "witt@b.com");
		jsonObject.put("family_name", "Wit");
		jsonObject.put("given_name", "Dean");
		jsonObject.put("nickname", "Naed");
		jsonObject.put("idempotency_key", customer.getIdempotencyKey());
		jsonObject.put("phone_number", "916-123-1234");
		
		
	    HttpEntity<String> request = 
	    	      new HttpEntity<String>(jsonObject.toString(), headers);
	    	    
		System.out.println("Request: \n" + request+"\n");		
		
		String res = restTemplate.postForObject(CUSTOMER_URL, request, String.class);		
		
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			JsonNode root = objectMapper.readTree(res);
			System.out.println("root: \n" + root+"\n");
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    /****************** https://www.concretepage.com/spring-5/spring-resttemplate-postforobject ***********/
		//String res = restTemplate.postForObject(CUSTOMER_URL, request, String.class);
		
		System.out.println("HttpResponse: \n" + res.toString()+"\n");
	    
		//MAY need to marshal the data into a new object from the cart post object, 
		//but for now I'm using the Square format for the Customer object 
		//so I can just play with it before submitting it here
		
	    /************************** https://developer.squareup.com/docs/customers-api/use-the-api/keep-records ******************/
	    	

//		curl https://connect.squareupsandbox.com/v2/customers \
//			  -X POST \
//			  -H 'Square-Version: 2021-04-21' \
//			  -H 'Authorization: Bearer {ACCESS_TOKEN}' \
//			  -H 'Content-Type: application/json' \
//			  -d '{
//			    "company_name": "ACME Inc.",
//			    "email_address": "john.doe.jr@acme.com",
//			    "family_name": "Doe",
//			    "given_name": "John",
//			    "nickname": "Junior",
//			    "idempotency_key": "59973bb6-75ae-497a-a006-b2490example",
//			    "phone_number": "+1 (206) 222-3456"
//			  }'
		
		/************************** https://www.baeldung.com/spring-resttemplate-post-json ******************/
		/*********** note HttpEntity<String> **********/
/*		
	    HttpEntity<String> request = 
	    	      new HttpEntity<String>(personJsonObject.toString(), headers);
	    	    
	    	    String personResultAsJsonStr = 
	    	      restTemplate.postForObject(createPersonUrl, request, String.class);
	    	    JsonNode root = objectMapper.readTree(personResultAsJsonStr);
*/		
		
		//create headers
//		  -H 'Square-Version: 2021-04-21' \
//		  -H 'Authorization: Bearer {ACCESS_TOKEN}' \
//		  -H 'Content-Type: application/json' \
		
		
	}

	@Override
	public void createInvoice(RawJsonEntity rawJsonEntity) {
		System.out.println("running createInvoice stub: " + rawJsonEntity + " \n\n");
		
	}

	@Override
	public void sendSms(RawJsonEntity rawJsonEntity) {
		System.out.println("running sendSms stub: " + rawJsonEntity + " \n\n");
		
	}
}
