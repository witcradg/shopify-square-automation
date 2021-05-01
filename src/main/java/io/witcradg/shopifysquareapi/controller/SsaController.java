package io.witcradg.shopifysquareapi.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;
import io.witcradg.shopifysquareapi.service.ICommunicatorService;

@RestController
public class SsaController {

	@Autowired
	ICommunicatorService communicatorService;

	@PostMapping("/ssa")
	public ResponseEntity<HttpStatus> save(@RequestBody String rawJson) {

		JSONObject jsonObject = new JSONObject(rawJson);

		try {
			CustomerOrder customerOrder = new CustomerOrder(jsonObject.getJSONObject("content"));
			communicatorService.createCustomer(customerOrder);
			communicatorService.createOrder(customerOrder);
			communicatorService.createInvoice(customerOrder);
			communicatorService.publishInvoice(customerOrder);
			communicatorService.sendSms(customerOrder);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
