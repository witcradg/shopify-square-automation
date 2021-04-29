package io.witcradg.shopifysquareapi.service;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;

@Service
public class CommunicatorServiceImpl implements ICommunicatorService {
	
	private RestTemplate restTemplate = new RestTemplate();
	private ObjectMapper objectMapper = new ObjectMapper();
	private HttpHeaders headers = new HttpHeaders();
	{
		headers.add("Square-Version", "2021-04-21");
	    headers.add("Authorization", "Bearer EAAAEDtqOqiRL-XbBm_RjAG4Nns-CBJbpmC4CmeIzO-35KQ-LyUymZYzvlg7dJh1"); //TODO token
	    headers.setContentType(MediaType.APPLICATION_JSON);
	}

	final private static String CUSTOMER_URL = "https://connect.squareupsandbox.com/v2/customers";
	
	
	@Override
	public void createCustomer(CustomerOrder customer) {

		System.out.println("createCustomer: " + customer.toString() + " \n\n");		


	    JSONObject addressObject = new JSONObject();
	    addressObject.put("address_line_1", customer.getAddressLine1());
	    addressObject.put("address_line_2", customer.getAddressLine2());
	    addressObject.put("address_line_3", customer.getAddressLine3());
	    addressObject.put("administrative_district_level_1", customer.getCity());
	    addressObject.put("administrative_district_level_2", customer.getState());
	    //addressObject.put("administrative_district_level_3", "level3");
	    addressObject.put("country", "US");
		addressObject.put("postal_code", customer.getPostalCode());
	    
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("company_name", customer.getCompanyName());
		requestBody.put("email_address", customer.getEmailAddress());
		requestBody.put("family_name", customer.getFamilyName());
		requestBody.put("given_name", customer.getGivenName());
		requestBody.put("nickname", customer.getNickname());
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("phone_number", customer.getPhoneNumber());
		requestBody.put("address", addressObject);

	    HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		System.out.println("request: \n" + request+"\n");

		String response = restTemplate.postForObject(CUSTOMER_URL, request, String.class);		
		System.out.println("response: \n" + response+"\n");
		
		try {
			JsonNode root = objectMapper.readTree(response);
			System.out.println("root: \n" + root+"\n");
			
		} catch (JsonProcessingException e) {
			// TODO some notification mechanism would be good. Email?
			e.printStackTrace();
		}
	}
	
	@Override
	public void createOrder(CustomerOrder customer) {
		System.out.println("createOrder: " + customer.toString() + " \n\n");		
	    JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());

		ArrayList<JSONObject> lineItemArray = new ArrayList<>();
		
		//for each item in the customerOrder create a JSONObject and add it to the array
		JSONObject lineItemObject = new JSONObject();
		//a lineItemObject must have both a description and a price (and a quantity?)
		
		lineItemObject.put("item", "an ordered item");
		lineItemObject.put("quantity", "1");
		//add it to the array of items
		lineItemArray.add(lineItemObject);
		
		System.out.println("\n lineItemArray\n" + lineItemArray.toString());
						
	    requestBody.put("order", "");
	}

	@Override
	public void createInvoice(CustomerOrder customer) {
		System.out.println("createInvoice: " + customer.toString() + " \n\n");	
	    JSONObject invoiceObject = new JSONObject();
	    invoiceObject.put("description", "some description line");
	    invoiceObject.put("order_number", customer.getInvoiceNumber());	
	    invoiceObject.put("order_total", customer.getInvoiceTotal());	
	    invoiceObject.put("idempotency_key", UUID.randomUUID().toString());

	    JSONObject requestBody = new JSONObject();
	    requestBody.put("invoice", invoiceObject);
	    HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		System.out.println("request: \n" + request+"\n");
	}

	@Override
	public void sendSms(CustomerOrder customer) {
		System.out.println("running sendSms stub: " + customer + " \n\n");
	}
}
