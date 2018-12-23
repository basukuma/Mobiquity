package com.mobiquityinc.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.model.Packet;
import com.mobiquityinc.model.PacketBean;
import com.mobiquityinc.model.PacketItem;
import com.mobiquityinc.util.PackerUtil;

public class PackerTest {

	private static final String FILE_PATH = "./src/test/resources/data.txt";

	/**
	 * Tests if the packets can be parsed
	 * 
	 * @throws IOException
	 */
	@Test
	public void testParsePacket() throws APIException {
		assertEquals(81, (int) PackerUtil.processPackerData(FILE_PATH).getPackets().get(0).getMaxWeight());
	}

	/**
	 * Tests the items to be chosen
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPickItemForPackageOne() throws APIException {
		assertEquals(4, PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(0)
				.getItems().get(0).getIndex());
	}

	/**
	 * Tests the items to be chosen
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPickItemForPackageTwo() throws APIException {

		List<PacketItem> result = PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(1)
				.getItems();
		assertTrue(result.isEmpty());
		;
	}

	/**
	 * Tests the items to be chosen
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPickItemForPackageThree() throws APIException {

		int[] actuals = new int[] {
				PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(2).getItems().get(1)
						.getIndex(),
				PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(2).getItems().get(0)
						.getIndex() };
		int[] expecteds = new int[] { 2, 7 };
		assertArrayEquals(expecteds, actuals);
	}

	/**
	 * Tests the items to be chosen
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPickItemForPackageFour() throws APIException {
		int[] actuals = new int[] {
				PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(3).getItems().get(1)
						.getIndex(),
				PackerUtil.pickPacketItem(PackerUtil.processPackerData(FILE_PATH)).getPackets().get(3).getItems().get(0)
						.getIndex() };
		int[] expecteds = new int[] { 8, 9 };
		assertArrayEquals(expecteds, actuals);
	}

	/**
	 * Tests the validation of the package max item cost (100)
	 *
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testValidateMaxCost() {
		PacketBean packetBean = new PacketBean();
		Packet packet = new Packet();
		packet.getItems().add(new PacketItem(1, 100, 200));
		packetBean.getPackets().add(packet);
		PackerUtil.validatePacketItems(packetBean);
	}

	/**
	 * Tests the validation of the package max item weight (100)
	 *
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testValidateMaxWeight() {
		PacketBean packetBean = new PacketBean();
		Packet packet = new Packet();
		packet.getItems().add(new PacketItem(1, 200, 85));
		packetBean.getPackets().add(packet);
		PackerUtil.validatePacketItems(packetBean);
	}

	/**
	 * Tests the validation of the package max cost (100)
	 *
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testValidateMaxPackageCost() {
		PacketBean packetBean = new PacketBean();
		Packet packet = new Packet();
		packet.setMaxWeight(200);
		packet.getItems().add(new PacketItem(1, 45, 35));
		packetBean.getPackets().add(packet);
		PackerUtil.validatePacketItems(packetBean);
	}

}
