package com.mobiquityinc.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

/**
 * Packet model
 * 
 * @author BSukumar
 */
public class Packet {

	@Max(value = 100, message = "Maximum weight of an Package should not be greater than 100")
	private double maxWeight = 0.0;

	@Size(max = 15, message = "Max 15 items allowed per package")
	private List<PacketItem> packetItems = new ArrayList<PacketItem>();

	public Packet() {
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public List<PacketItem> getItems() {
		return packetItems;
	}

	public void setItems(List<PacketItem> packetItems) {
		this.packetItems = packetItems;
	}
}