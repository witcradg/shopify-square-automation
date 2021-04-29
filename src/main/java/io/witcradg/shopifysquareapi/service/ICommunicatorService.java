package io.witcradg.shopifysquareapi.service;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;
import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

public interface ICommunicatorService {
	public abstract void createCustomer(CustomerOrder customer);
	public abstract void createOrder(CustomerOrder customer);
	public abstract void createInvoice(CustomerOrder customer);
	public abstract void sendSms(CustomerOrder customer);
}
