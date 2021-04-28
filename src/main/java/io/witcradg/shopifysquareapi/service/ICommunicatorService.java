package io.witcradg.shopifysquareapi.service;

import io.witcradg.shopifysquareapi.entity.Customer;
import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

public interface ICommunicatorService {
	public abstract void createCustomer(Customer customer);
	public abstract void createInvoice(RawJsonEntity rawJsonEntity);
	public abstract void sendSms(RawJsonEntity rawJsonEntity);
}
