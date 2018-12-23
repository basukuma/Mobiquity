package com.mobiquityinc.packer;

import java.util.logging.Logger;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.model.PacketBean;
import com.mobiquityinc.util.PackerUtil;

/**
 * The class Packer for receiving the input file from the user and processing
 * the file.
 * 
 * @author BSukumar
 */

public class Packer {

	private static final Logger log = Logger.getLogger(Packer.class.getName());

	/**
	 * Main method of the application.
	 * 
	 * @param args
	 *            filepath as runtime arguments from user
	 */
	public static void main(String[] args) throws APIException {

		// Check for the input arguments
		if (args.length == 0 || args[0] != null && args[0].trim().isEmpty()) {
			throw new APIException("Missing argument(s), input file path");
		}
		String output = pack(args[0]);
		log.info("Output: " + output);
	}

	/**
	 * This method uses utility method to perform the below operations. 
	 * Receiving the input file from the user and processing the file. 
	 * Determine the items to put into the package. 
	 * String representation of the indexes of the selected items.
	 * 
	 * @param filePath
	 *            - The file path containing the packet items data
	 * @return String of index numbers of the selected items
	 * @throws APIException
	 */
	public static String pack(String filePath) throws APIException {
		// Receiving the input file from the user and processing the file.
		PacketBean packetBean = PackerUtil.processPackerData(filePath);
		
		//Validate the packet items for maximum weight and cost
		PackerUtil.validatePacketItems(packetBean);
		//  Determine the items to put into the package. 
		PacketBean pickedPacketBean = PackerUtil.pickPacketItem(packetBean);

		return PackerUtil.generateOutput(pickedPacketBean);

	}

}
