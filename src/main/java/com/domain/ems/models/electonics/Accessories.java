package com.domain.ems.models.electonics;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "accessories")
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Accessories {

	private long id;
	@Column(unique = true)
	private String name;
	private String category;
	private String brand;
	private long price;
	private long inventory;

	
	private Set<Gadgets> gadgets;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(targetEntity = Gadgets.class ,cascade = CascadeType.ALL )
	public Set<Gadgets> getGadgets() {
		return gadgets;
	}

	public void setGadgets(Set<Gadgets> gadgets) {
		this.gadgets = gadgets;
	}

	@JsonProperty("inventory")
	public long getInventory() {
		return inventory;
	}

	@JsonProperty("inventory")
	public void setInventory(long inventory) {
		this.inventory = inventory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

}
