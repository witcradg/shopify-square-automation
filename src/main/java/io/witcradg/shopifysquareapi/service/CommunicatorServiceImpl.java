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
		headers.add("Authorization", "Bearer EAAAEDtqOqiRL-XbBm_RjAG4Nns-CBJbpmC4CmeIzO-35KQ-LyUymZYzvlg7dJh1"); // TODO
																													// token
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	final private static String CUSTOMER_URL = "https://connect.squareupsandbox.com/v2/customers";
	final private static String ORDER_URL = "https://connect.squareupsandbox.com/v2/orders";
	final private static String INVOICE_URL = "https://connect.squareupsandbox.com/v2/invoices";

	@Override
	public void createCustomer(CustomerOrder customerOrder) throws Exception {

		System.out.println("createCustomer: " + customerOrder.toString() + " \n\n");

		JSONObject addressObject = new JSONObject();
		addressObject.put("address_line_1", customerOrder.getAddressLine1());
		addressObject.put("address_line_2", customerOrder.getAddressLine2());
		addressObject.put("address_line_3", customerOrder.getAddressLine3());
		addressObject.put("administrative_district_level_1", customerOrder.getCity());
		addressObject.put("administrative_district_level_2", customerOrder.getState());
		// addressObject.put("administrative_district_level_3", "level3");
		addressObject.put("country", "US");
		addressObject.put("postal_code", customerOrder.getPostalCode());

		JSONObject requestBody = new JSONObject();
		requestBody.put("company_name", customerOrder.getCompanyName());
		requestBody.put("email_address", customerOrder.getEmailAddress());
		requestBody.put("family_name", customerOrder.getFamilyName());
		requestBody.put("given_name", customerOrder.getGivenName());
		requestBody.put("nickname", customerOrder.getNickname());
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("phone_number", customerOrder.getPhoneNumber());
		requestBody.put("address", addressObject);

		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		System.out.println("request: \n" + request + "\n");

		String response = restTemplate.postForObject(CUSTOMER_URL, request, String.class);
		System.out.println("response: \n" + response + "\n");

		JSONObject responseCustomer = new JSONObject(response);
		String id = responseCustomer.getJSONObject("order").getString("id");
		System.out.println("\n responseCustomer: " + id);
		customerOrder.setSquareCustomerId(id);

	}

	@Override
	public void createOrder(CustomerOrder customerOrder) throws Exception {
		System.out.println("createOrder: " + customerOrder.toString() + " \n\n");

		JSONObject basePriceMoney = new JSONObject();
		basePriceMoney.put("amount", customerOrder.getInvoiceTotal());
		basePriceMoney.put("currency", "USD");

		JSONObject lineItem = new JSONObject();
		lineItem.put("quantity", "1");
		lineItem.put("base_price_money", basePriceMoney);
		lineItem.put("name", customerOrder.getInvoiceNumber());

		ArrayList<JSONObject> lineItemArray = new ArrayList<>();
		lineItemArray.add(lineItem);

		JSONObject order = new JSONObject();
		order.put("location_id", "LGWCV6JAAE6JV");
		order.put("line_items", lineItemArray);

		JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("order", order);

		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

		System.out.println("request: \n" + request + "\n");

		String response = restTemplate.postForObject(ORDER_URL, request, String.class);
		System.out.println("response: \n" + response + "\n");

		JSONObject responseOrder = new JSONObject(response);
		String id = responseOrder.getJSONObject("order").getString("id");
		System.out.println("\n responseOrder: " + id);
		customerOrder.setSquareOrderId(id);
	}

	@Override
	public void createInvoice(CustomerOrder customerOrder) {
		System.out.println("createInvoice: " + customerOrder.toString() + " \n\n");
		JSONObject invoiceObject = new JSONObject();
		invoiceObject.put("description", "some description line");
		invoiceObject.put("order_number", customerOrder.getInvoiceNumber());
		invoiceObject.put("order_total", customerOrder.getInvoiceTotal());
		invoiceObject.put("idempotency_key", UUID.randomUUID().toString());

		JSONObject requestBody = new JSONObject();
		requestBody.put("invoice", invoiceObject);
		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		System.out.println("request: \n" + request + "\n");

		String response = restTemplate.postForObject(ORDER_URL, request, String.class);
		System.out.println("response: \n" + response + "\n");

		JSONObject responseInvoice = new JSONObject(response);
		String id = responseInvoice.getJSONObject("order").getString("id");
		System.out.println("\n root: " + id);
	}

	@Override
	public void sendSms(CustomerOrder customerOrder) {
		System.out.println("running sendSms stub: " + customerOrder + " \n\n");
	}
}
