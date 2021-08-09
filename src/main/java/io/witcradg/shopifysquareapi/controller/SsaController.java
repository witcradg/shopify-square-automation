package io.witcradg.shopifysquareapi.controller;

import java.util.Enumeration;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

		try {
			JSONObject jsonObject = new JSONObject(rawJson);

			if ("order.completed".equals(jsonObject.getString("eventName"))) {

				CustomerOrder customerOrder = new CustomerOrder(jsonObject.getJSONObject("content"));
				communicatorService.createCustomer(customerOrder);
				communicatorService.createOrder(customerOrder);
				communicatorService.createInvoice(customerOrder);
				communicatorService.publishInvoice(customerOrder);
//				communicatorService.sendSms(customerOrder);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("rawJson: " + rawJson);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "**")
	public ResponseEntity<HttpStatus> error(HttpServletRequest request) {
		dumpRequest(request);

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	private void dumpRequest(HttpServletRequest request) {
		Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
		if (exception != null) {
			log.error("    exception: " + exception.getMessage());
		}
		log.error("    Request URL: " + request.getRequestURL());

		StringBuffer headers = new StringBuffer();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.append(key +":"+ value +",");
        }
        log.error("headers" + headers );
		
		try {
			log.error(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
		} catch (Exception e) {
			log.error("Exception thrown in error method when trying to read request: " + e.getMessage());
		}
	}
}
