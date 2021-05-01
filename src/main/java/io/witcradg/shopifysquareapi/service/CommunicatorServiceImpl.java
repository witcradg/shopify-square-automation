package io.witcradg.shopifysquareapi.service;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;

@Service
public class CommunicatorServiceImpl implements ICommunicatorService {

	private static final String CUSTOMER_URL = "https://connect.squareupsandbox.com/v2/customers";
	private static final String ORDER_URL = "https://connect.squareupsandbox.com/v2/orders";
	private static final String INVOICE_URL = "https://connect.squareupsandbox.com/v2/invoices";
	
	private static final DateTimeFormatter fromFormat = DateTimeFormatter.ofPattern("yyyy-dd-MM", Locale.ENGLISH);
	private static final DateTimeFormatter toFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
	
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders headers = new HttpHeaders();
	{
		headers.add("Square-Version", "2021-04-21");
		headers.add("Authorization", "Bearer EAAAEDtqOqiRL-XbBm_RjAG4Nns-CBJbpmC4CmeIzO-35KQ-LyUymZYzvlg7dJh1"); // TODO
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

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
		//requestBody.put("nickname", customerOrder.getNickname());
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
		customerOrder.setSqCustomerId(id);

	}

	@Override
	public void createOrder(CustomerOrder customerOrder) throws Exception {
		System.out.println("createOrder: " + customerOrder.toString() + " \n\n");

		JSONObject basePriceMoney = new JSONObject();
		basePriceMoney.put("amount", customerOrder.getScInvoiceTotal());
		basePriceMoney.put("currency", "USD");

		JSONObject lineItem = new JSONObject();
		lineItem.put("quantity", "1");
		lineItem.put("base_price_money", basePriceMoney);
		lineItem.put("name", customerOrder.getScInvoiceNumber());

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
		customerOrder.setSqOrderId(id);
	}

	@Override
	public void createInvoice(CustomerOrder customerOrder) {
		System.out.println("createInvoice: " + customerOrder.toString() + " \n\n");
		
		JSONObject acceptedPaymentMethods = new JSONObject();
		acceptedPaymentMethods.put("bank_account", false); //TODO string or boolean?
		acceptedPaymentMethods.put("card", true); //TODO string or boolean?
		acceptedPaymentMethods.put("square_gift_card", false);
		

		JSONObject paymentRequest = new JSONObject();
		paymentRequest.put("automatic_payment_source", "NONE");
		
		String orderDateRFC3339 = customerOrder.getScOrderDate();
		System.out.println("orderDateRFC3339 " + orderDateRFC3339);

		Instant instant = Instant.parse(orderDateRFC3339);
		String strInstant = instant.toString();
		System.out.println("strInstant " + strInstant);
		
		Instant test = Instant.parse("2017-10-04T00:00:00Z");
		System.out.println("test " + test);
		
		boolean res = instant.isAfter(test);
		System.out.println("res " + res);
		
//		String orderDtYDM = customerOrder.getScOrderDate().substring(0, 10);
//		
//		String orderDtYDM = customerOrder.getScOrderDate().substring(0, 10);
//		System.out.println("order date: " + orderDtYDM);
//		
//		LocalDate dateTime = LocalDate.parse(orderDtYDM, fromFormat);
//		String orderDtYMD = dateTime.format(toFormat);
//		
//		System.out.println("order date: " + orderDtYMD);

//		paymentRequest.put("due_date", orderDtYMD);
		paymentRequest.put("request_type", "BALANCE");
		
		JSONArray paymentRequests = new JSONArray();
		paymentRequests.put(paymentRequest);
		
		JSONObject invoiceObject = new JSONObject();
		invoiceObject.put("accepted_payment_methods", acceptedPaymentMethods);
		invoiceObject.put("delivery_method", "SHARE_MANUALLY");
		invoiceObject.put("invoice_number", customerOrder.getScInvoiceNumber());
		invoiceObject.put("location_id", "LGWCV6JAAE6JV");
		invoiceObject.put("order_id", customerOrder.getSqOrderId());
		invoiceObject.put("payment_requests", paymentRequests);
		invoiceObject.put("scheduled_at", customerOrder.getScOrderDate());
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("invoice", invoiceObject);
	
		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		System.out.println("request: \n" + request + "\n");
		
//		String response = restTemplate.postForObject(INVOICE_URL, request, String.class);
//		System.out.println("response: \n" + response + "\n");
//
//		JSONObject responseInvoice = new JSONObject(response);
//		String id = responseInvoice.getJSONObject("invoice").getString("id");
//		System.out.println("\n root: " + id);
//		customerOrder.setSqInvoiceId(id);
	}

	@Override
	public void sendSms(CustomerOrder customerOrder) {
		System.out.println("running sendSms stub: " + customerOrder + " \n\n");
	}
}
