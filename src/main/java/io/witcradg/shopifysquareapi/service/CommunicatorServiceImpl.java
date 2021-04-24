package io.witcradg.shopifysquareapi.service;

import org.springframework.stereotype.Service;

import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

@Service
public class CommunicatorServiceImpl implements ICommunicatorService {

	@Override
	public void createCustomer(RawJsonEntity rawJsonEntity) {
		System.out.println("running createCustomer stub");
	
	}

	@Override
	public void createInvoice(RawJsonEntity rawJsonEntity) {
		System.out.println("running createInvoice stub");
		
	}

	@Override
	public void sendSms(RawJsonEntity rawJsonEntity) {
		System.out.println("running sendSms stub");
		
	}

}
