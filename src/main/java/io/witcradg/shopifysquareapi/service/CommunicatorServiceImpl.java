package io.witcradg.shopifysquareapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

@Service
public class CommunicatorServiceImpl implements ICommunicatorService {
	
	RestTemplate restTemplate = new RestTemplate();

	@Override
	public void createCustomer(RawJsonEntity rawJsonEntity) {
		String response = restTemplate.getForObject("https://httpbin.org/ip", String.class);
		System.out.println("running createCustomer stub: " + response + " \n\n");
	
	}

	@Override
	public void createInvoice(RawJsonEntity rawJsonEntity) {
		String response = restTemplate.getForObject("https://cat-fact.herokuapp.com/facts", String.class);		
		System.out.println("running createInvoice stub: " + response + " \n\n");
		
	}

	@Override
	public void sendSms(RawJsonEntity rawJsonEntity) {
		String response = restTemplate.getForObject("https://api.coindesk.com/v1/bpi/currentprice.json", String.class);
		System.out.println("running sendSms stub: " + response + " \n\n");
		
	}

}
