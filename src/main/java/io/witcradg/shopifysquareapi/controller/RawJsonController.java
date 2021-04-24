package io.witcradg.shopifysquareapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.witcradg.shopifysquareapi.entity.RawJsonEntity;
import io.witcradg.shopifysquareapi.repository.IRawJsonRepository;

@RestController
public class RawJsonController {
	
	@Autowired 
	IRawJsonRepository rawJsonRepo;
	
	
	@PutMapping("/ssa")
	public ResponseEntity<RawJsonEntity> save( @RequestBody RawJsonEntity rawJsonEntity) {
		System.err.println(
				String.format("rawJsonEntity %s", rawJsonEntity.getRawJson()));
		try {
			return new ResponseEntity<>(rawJsonRepo.save(rawJsonEntity), HttpStatus.CREATED);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
