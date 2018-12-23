package com.mobiquityinc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet model representing a container of a Packet list
 * 
 * @author BSukumar
 */
public class PacketBean {
	
	public PacketBean() {
	}
	
	public PacketBean(List<Packet> packets) {
		this.packets = packets;
	}

	private List<Packet> packets = new ArrayList<Packet>();

	public List<Packet> getPackets() {
		return packets;
	}

	public void setPackets(List<Packet> packets) {
		this.packets = packets;
	}
}