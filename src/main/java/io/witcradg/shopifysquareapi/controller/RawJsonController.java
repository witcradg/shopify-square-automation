package io.witcradg.shopifysquareapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.witcradg.shopifysquareapi.entity.Customer;
import io.witcradg.shopifysquareapi.entity.RawJsonEntity;
import io.witcradg.shopifysquareapi.repository.IRawJsonRepository;
import io.witcradg.shopifysquareapi.service.ICommunicatorService;

@RestController
public class RawJsonController {
	
	@Autowired 
	IRawJsonRepository rawJsonRepo;
	
	@Autowired
	ICommunicatorService communicatorService;
	
	@PutMapping("/string")
	public ResponseEntity<RawJsonEntity> save( @RequestBody RawJsonEntity rawJsonEntity) {
		
//		communicatorService.createCustomer(rawJsonEntity);
//		communicatorService.createInvoice(rawJsonEntity);
//		communicatorService.sendSms(rawJsonEntity);
		
		System.err.println(
				String.format("rawJsonEntity %s", rawJsonEntity.getRawJson()));
		try {
			return new ResponseEntity<>(rawJsonRepo.save(rawJsonEntity), HttpStatus.I_AM_A_TEAPOT);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/ssa")
	public ResponseEntity<String> save( @RequestBody Customer customer) {
	
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
