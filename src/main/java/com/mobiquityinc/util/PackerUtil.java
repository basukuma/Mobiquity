package com.mobiquityinc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.model.Packet;
import com.mobiquityinc.model.PacketBean;
import com.mobiquityinc.model.PacketItem;

/**
 * This utility class is to create Packet model by parsing the input file and to
 * determine the items to put into the package so that the total weight of the
 * package is less than or equal to the package limit and the total cost is as
 * large as possible using dynamic programming algorithm.
 * 
 * @author BSukumar
 * 
 */
public class PackerUtil {
	
	private static final Logger log = Logger.getLogger(PackerUtil.class.getName());
	
	/**
	 * This method parse each line in the given file and map it to PacketItem
	 * model. Each PacketItem has parameters as index number, weight and cost.
	 * 
	 * @param filePath
	 *            the path of the file to be parsed into packs
	 * @return Packet containing list of items
	 **/
	public static PacketBean processPackerData(String filePath) throws APIException {
		log.info("Processing input file");
		PacketBean packetBean = new PacketBean();
		Pattern pattern = Pattern.compile("\\d+\\.?\\d*");

		// Read each line from file
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

			stream.forEach(line -> {
				Matcher matcher = pattern.matcher(line);
				Packet packet = new Packet();
				int idx = 0;
				PacketItem packetItem = new PacketItem();
				while (matcher.find()) {
					if (matcher.start() == 0) {
						packet.setMaxWeight(Double.parseDouble(matcher.group()));
					} else {
						idx++;

						if (idx == 1) {
							packetItem.setIndex(Integer.parseInt(matcher.group()));
						}
						if (idx == 2) {
							packetItem.setWeight(Double.parseDouble(matcher.group()));
						}
						if (idx == 3) {
							packetItem.setCost(Double.parseDouble(matcher.group()));
							packet.getItems().add(packetItem);
							packetItem = new PacketItem();
							idx = 0;
						}

					}
				}
				
				packetBean.getPackets().add(packet);
			});

		} catch (IOException e) {
			throw new APIException("Error processing file - " + filePath, e);
		} catch (Exception e) {
			throw new APIException("Error processing file - " + filePath, e);
		}
		log.info("File Processed successfully");
		return packetBean;
	}

	/**
	 * This method is to determine items to put into the package so that the
	 * total weight of the package is less than or equal to the package limit
	 * and the total cost is as large as possible.
	 * 
	 * The items to be selected is determined using dynamic programming using the below formula
	 * Define m[i,w] to be the maximum value that can be attained with weight less than or 
	 * equal to w using the items upto i(first i items)
	 * m[0,w]=0
	 * m[i,w]=m[i - 1,w] if (the new item is more than the current weight limit)
	 * m[i][w] = max(m[i - 1,w],m[i - 1,w-itemWeight]+itemcost)
	 * 
	 * @param packetBean
	 *            Packet containing list of items
	 * @return packetBean 
	 *             Packet containing chosen items to put in to the package
	 **/
	public static PacketBean pickPacketItem(PacketBean packetBean) throws APIException {
		log.info("Selecting items to put in to the package");
		PacketBean pickedPacketBean = new PacketBean();
		try {
			packetBean.getPackets().stream().forEach(packet -> {

				int maxWeight = (int) packet.getMaxWeight();
				// Get the total number of items.
				int noOfItems = packet.getItems().size();
				// Create a matrix. Items are in rows and weight at in columns+1 on each side
				int[][] valuearr = new int[noOfItems + 1][maxWeight + 1];

				for (int col = 0; col <= maxWeight; col++) {
					valuearr[0][col] = 0;
				}

				for (int row = 0; row <= noOfItems; row++) {
					valuearr[row][0] = 0;
				}

				for (int i = 1; i <= noOfItems; i++) {
					int itemWeight = (int) packet.getItems().get(i - 1).getWeight();
					int itemCost = (int) packet.getItems().get(i - 1).getCost();
					for (int w = 1; w <= maxWeight; w++) {
						// Is the current items weight less than or equal to
						// running weight
						if (itemWeight <= w) {
							// Given a weight, check if the value of the current
							// item + value of the item that we could afford
							// with the remaining weight is greater than the value
							// without the current item itself
							valuearr[i][w] = Math.max(itemCost + valuearr[i - 1][w - itemWeight], valuearr[i - 1][w]);
						} else {
							// If the current item's weight is more than the running weight, just carry forward the value
							// without the current item
							valuearr[i][w] = valuearr[i - 1][w];
						}
					}
				}

				// selecting the items to collect in the package
				Packet filteredpacket = new Packet();
				List<PacketItem> filteredItem = new ArrayList<PacketItem>();
				int i = noOfItems;
				int j = maxWeight;
				while (i > 0 && j > 0) {
					if (valuearr[i][j] == valuearr[i - 1][j]) {
						i--;
					} else {
						filteredItem.add(packet.getItems().get(i - 1));
						int item_weight = (int) packet.getItems().get(i - 1).getWeight();
						i--;
						j = j - item_weight;
					}
				}
				
				filteredpacket.setMaxWeight(maxWeight);
				filteredpacket.setItems(filteredItem);
				
				// pick the item less in weight, if the items with same total cost are found
				packet.getItems().stream().collect(Collectors.groupingBy(PacketItem::getCost))
						.forEach((cost, itemsFound) -> {
							if (itemsFound.size() > 1) {
								PacketItem itemWithLessWeight = itemsFound.stream()
										.min(Comparator.comparing(PacketItem::getWeight)).orElse(null);
								if (itemWithLessWeight != null) {
									if (!filteredpacket.getItems().contains(itemWithLessWeight)) {
										filteredpacket.getItems().removeAll(itemsFound);
										filteredpacket.getItems().add(itemWithLessWeight);
									}

								}
							}
						});				
				
				pickedPacketBean.getPackets().add(filteredpacket);
			});			

		} catch (Exception e) {
			throw new APIException("Error in choosing package items to put in the package", e);
		}
		log.info("Items selected successfully");
		return pickedPacketBean;
	}

	
	/**
	 * This method returns the String, containing the index of the chosen items,
	 * split by commas.
	 *
	 * @param pickedPacketBean
	 *            Packet containing chosen items to put in to the package
	 * 
	 * @return a String representation of the indexes of the items
	 */
	public static String generateOutput(PacketBean pickedPacketBean) throws APIException {
		log.info("Generating Output format");
		StringBuilder sb = new StringBuilder();
		try {

			pickedPacketBean.getPackets().forEach(packet -> {
				sb.append(System.getProperty("line.separator"));
				sb.append(" Selected Packet Index Numbers =[");

				if (!packet.getItems().isEmpty()) {
					// Sort the item indexes and create string
					sb.append(packet.getItems().stream()
							.sorted((item1, item2) -> Integer.compare(item1.getIndex(), item2.getIndex()))
							.map(item -> String.valueOf(item.getIndex())).collect(Collectors.joining(", ")));
				}

				sb.append("]");
			});
		} catch (Exception e) {
			throw new APIException("Error Generating Output format - ", e);
		}

		return sb.toString();
	}

	/**
	 * This method validate the packet items for maximum weight and cost
	 *
	 * @param packetBean
	 *            Packet containing list of items
	 * @throws APIException
	 */
	public static boolean validatePacketItems(PacketBean packetBean) {

		log.info("Validate items to put into the package");
		packetBean.getPackets().stream().forEach(packet -> {
			Set<ConstraintViolation<Packet>> invalidPackets = ValidationUtil.getInstance().validate(packet);

			if (!invalidPackets.isEmpty()) {
				throw new ConstraintViolationException(invalidPackets);
			}

			if (!packet.getItems().isEmpty()) {
				packet.getItems().stream().forEach(packetItem -> {

					Set<ConstraintViolation<PacketItem>> invalidPacketItems = ValidationUtil.getInstance()
							.validate(packetItem);
					if (!invalidPacketItems.isEmpty()) {
						throw new ConstraintViolationException(invalidPacketItems);
					}
				});
			}

		});	
		
		return true;
	}
	
}
