package com.mobiquityinc.model;

import javax.validation.constraints.Max;

/**
 * PacketItem model representing an item from a Packet
 * 
 * @author BSukumar
 */
public class PacketItem {

	private int index = 0;
	@Max(value = 100, message = "Weight of an item should not be greater than 100")
	private double weight = 0.0;
	@Max(value = 100, message = "Cost of an item should not be greater than 100")
	private double cost = 0.0;

	public PacketItem() {
	}

	public PacketItem(int index, double weight, double cost) {
		super();
		this.index = index;
		this.weight = weight;
		this.cost = cost;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}