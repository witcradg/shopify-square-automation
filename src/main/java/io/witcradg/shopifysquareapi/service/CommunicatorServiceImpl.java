package io.witcradg.shopifysquareapi.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;
import lombok.extern.log4j.Log4j2;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Log4j2
@Service
public class CommunicatorServiceImpl implements ICommunicatorService {

	@Value("${configuration.square.url}")
	private String url_base;
	
	@Value("${configuration.square.access}")
	private String auth;
	
	@Value("${configuration.square.location}")
	private String location;
	
	@Value("${configuration.square.delivery}")
	private String delivery;
	
	@Value("${twilio.account.sid}")
	private String twilioSid;
	
	@Value("${twilio.auth.token}")
	private String twilioAuthToken;
	
	@Value("${twilio.phone.number}")
	private String twilioPhoneNumber;
		
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders headers = new HttpHeaders();

	@PostConstruct
	private void loadHeaders() {
		headers.add("Square-Version", "2021-04-21");		
		headers.add("Authorization", "Bearer "+ auth);
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@Override
	public void createCustomer(CustomerOrder customerOrder) throws Exception {
		
		log.debug("createCustomer: " + customerOrder.toString());

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
		//requestBody.put("company_name", customerOrder.getCompanyName());
		requestBody.put("email_address", customerOrder.getEmailAddress());
		requestBody.put("family_name", customerOrder.getFamilyName());
		requestBody.put("given_name", customerOrder.getGivenName());
		//requestBody.put("nickname", customerOrder.getNickname());
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("phone_number", customerOrder.getPhoneNumber());
		requestBody.put("address", addressObject);

		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		log.debug("request: " + request.getBody());

		String response = restTemplate.postForObject(url_base+"/customers", request, String.class);
		log.debug("response: " + response);

		JSONObject responseCustomer = new JSONObject(response);
		String id = responseCustomer.getJSONObject("customer").getString("id");
		log.debug("responseCustomer id: " + id);
		customerOrder.setSqCustomerId(id);
	}

	@Override
	public void createOrder(CustomerOrder customerOrder) throws Exception {
		log.debug("createOrder: " + customerOrder.toString());

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
		order.put("location_id", location);
		order.put("line_items", lineItemArray);

		JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("order", order);

		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		log.debug("request: " + request.getBody());
		
		String response = restTemplate.postForObject(url_base+"/orders", request, String.class);

		JSONObject responseOrder = new JSONObject(response);
		String id = responseOrder.getJSONObject("order").getString("id");
		log.debug(" responseOrder id: " + id);
		customerOrder.setSqOrderId(id);
	}

	@Override
	public void createInvoice(CustomerOrder customerOrder) {
		log.debug("createInvoice: " + customerOrder.toString());
		
		JSONObject primaryRecipient = new JSONObject();
		primaryRecipient.put("customer_id", customerOrder.getSqCustomerId());
		
		JSONObject acceptedPaymentMethods = new JSONObject();
		acceptedPaymentMethods.put("bank_account", false); //TODO string or boolean?
		acceptedPaymentMethods.put("card", true); //TODO string or boolean?
		acceptedPaymentMethods.put("square_gift_card", false);

		JSONObject paymentRequest = new JSONObject();
		paymentRequest.put("automatic_payment_source", "NONE");

		paymentRequest.put("request_type", "BALANCE");
		
		JSONArray paymentRequests = new JSONArray();
		paymentRequests.put(paymentRequest);
		
		JSONObject invoiceObject = new JSONObject();
		invoiceObject.put("accepted_payment_methods", acceptedPaymentMethods);
		invoiceObject.put("delivery_method", delivery);
		invoiceObject.put("invoice_number", customerOrder.getScInvoiceNumber());
		invoiceObject.put("location_id", location);
		invoiceObject.put("order_id", customerOrder.getSqOrderId());
		invoiceObject.put("payment_requests", paymentRequests);

		
		//Date operations
		
		Instant scheduledInstant = Instant.now().plus(2, ChronoUnit.MINUTES);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone( ZoneId.of("UTC"));
		String scheduledAt = formatter.format(scheduledInstant);

		log.debug("scheduled_at: " + scheduledAt);
		invoiceObject.put("scheduled_at", scheduledAt);
		
		String dueDate = scheduledInstant.toString().substring(0,10);
		log.debug("due_date: " + dueDate);
		paymentRequest.put("due_date", dueDate);
		
		invoiceObject.put("primary_recipient",primaryRecipient);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("invoice", invoiceObject);
	
		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		log.debug("request: " + request.getBody());
		
		String response = restTemplate.postForObject(url_base+"/invoices", request, String.class);
		log.debug("response: \n" + response);

		JSONObject responseInvoice = new JSONObject(response);
		JSONObject invoice = responseInvoice.getJSONObject("invoice");
		String id = invoice.getString("id");
		int version = invoice.getInt("version");
		log.debug("invoice id: " + id);
		log.debug("invoice version: " + version);
		customerOrder.setSqInvoiceId(id);
		customerOrder.setSqInvoiceVersion(version);
	}
	
	@Override
	public void publishInvoice(CustomerOrder customerOrder) {
		log.debug("running publishInvoice: " + customerOrder);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("idempotency_key", UUID.randomUUID().toString());
		requestBody.put("version", customerOrder.getSqInvoiceVersion().intValue());
		
		HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
		log.debug("request: " + request.getBody());
		String publishURL = String.format(url_base+"/invoices/%s/publish", customerOrder.getSqInvoiceId());
		String response = restTemplate.postForObject(publishURL, request, String.class);
		log.debug("response publish invoice: " + response);
		
		// convert the response String to a json object
		JSONObject responseInvoice = new JSONObject(response);
		log.debug("responseInvoice: " + responseInvoice);
		
		// from the response object get the invoice
		JSONObject invoiceObject = responseInvoice.getJSONObject("invoice");
		log.debug("invoiceObject: " + invoiceObject);
		
//		String publicURL = invoiceObject.getString("public_url");
//		log.debug("publicURL: " + publicURL);

		//work-around
		String publicURL = "https://squareup.com/pay-invoice/" + invoiceObject.getString("id");
		log.debug("publicURL for setPaymentURL: " + publicURL);
		
		customerOrder.setPaymentURL(publicURL);
	}

	@Override
	public void sendSms(CustomerOrder customerOrder) throws InvalidPhoneNumberException {
		log.debug("running sendSms: " + customerOrder);

		String str = customerOrder.getPhoneNumber();

		StringBuilder stringBuilder = new StringBuilder();

		for (char dig : str.toCharArray()) {
		    if (Character.isDigit(dig)) 
		    {
		    	stringBuilder.append(dig);
		    }
		}
		
		String tmp = stringBuilder.toString();
		
		if (tmp.length() == 10 ) {
			stringBuilder.insert(0, "+1");
		} else if (tmp.length() == 11 && tmp.startsWith("1")) {
			stringBuilder.insert(0, '+');
		} else if (tmp.length() != 12 || !tmp.startsWith("+1")) {
			log.info("invalid phone number " + customerOrder.getPhoneNumber() );
			throw new InvalidPhoneNumberException(customerOrder.getPhoneNumber());
		}
		
		String sendTo = stringBuilder.toString();
		log.info("sendTo: " + sendTo);
				
		String messageContent = String.format(
				"Thank You for your Order on Delta8gummies.com. Use this link to Complete Your Purchase: %s " +
				"**Be Advised It takes up to 2 minutes before you can Complete Your Payment**" , 
				customerOrder.getPaymentURL());
		
		Twilio.init(twilioSid, twilioAuthToken);
		
		Message message = Message.creator(
				new PhoneNumber(sendTo), 
				new PhoneNumber(twilioPhoneNumber), 
				messageContent)
				.create();
		
		log.info("twilio message sid: " + message.getSid() );
	}
}
