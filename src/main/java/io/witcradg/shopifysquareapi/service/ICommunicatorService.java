package io.witcradg.shopifysquareapi.service;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;

public interface ICommunicatorService {
	public abstract void createCustomer(CustomerOrder customerOrder) throws Exception;
	public abstract void createOrder(CustomerOrder customerOrder) throws Exception;
	public abstract void createInvoice(CustomerOrder customerOrder) throws Exception;
	public abstract void publishInvoice(CustomerOrder customerOrder) throws Exception;
	public abstract void sendSms(CustomerOrder customerOrder) throws Exception;
}
