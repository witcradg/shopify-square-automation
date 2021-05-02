package io.witcradg.shopifysquareapi.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.witcradg.shopifysquareapi.entity.CustomerOrder;
import io.witcradg.shopifysquareapi.service.ICommunicatorService;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "**")
	public ResponseEntity<HttpStatus> error() {
		log.error("ERROR: Invalid request received");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
