package io.witcradg.shopifysquareapi.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name="raw_json_table")
@Entity
@Setter
@Getter
@ToString
public class RawJsonEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="raw_json")
	private String rawJson;
	
	@CreationTimestamp
	@Column(name="create_dtm")
	private Timestamp createDtm;
	
	
}
