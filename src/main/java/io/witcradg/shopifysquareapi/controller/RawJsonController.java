package io.witcradg.shopifysquareapi.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;
import io.witcradg.shopifysquareapi.entity.RawJsonEntity;
import io.witcradg.shopifysquareapi.repository.IRawJsonRepository;
import io.witcradg.shopifysquareapi.service.ICommunicatorService;

@RestController
public class RawJsonController {
	
	@Autowired 
	IRawJsonRepository rawJsonRepo;
	
	@Autowired
	ICommunicatorService communicatorService;
	
	@PostMapping("/cart")
	public ResponseEntity<HttpStatus> save( @RequestBody String rawJson) {

	    JSONObject jsonObject = new JSONObject(rawJson);
		
		

		try {
			CustomerOrder customerOrder = new CustomerOrder(jsonObject.getJSONObject("content"));
//			communicatorService.createCustomer(customerOrder);
//			communicatorService.createOrder(customerOrder);
			communicatorService.createInvoice(customerOrder);
//			communicatorService.sendSms(customerOrder);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/ssa")
	public ResponseEntity<String> save( @RequestBody CustomerOrder customer) {
	
		try {
			//communicatorService.createCustomer(customer);
			communicatorService.createInvoice(customer);
			return ResponseEntity.ok("Customer Created");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
