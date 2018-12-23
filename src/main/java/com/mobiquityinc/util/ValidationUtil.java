package com.mobiquityinc.util;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Utility class to Bootstrap ValidatorFactory
 * 
 */
public class ValidationUtil {

	private static Validator validator = null;

	/**
	 * Create instance of validator
	 */
	public static Validator getInstance() {
		if (validator == null) {
			Configuration<?> config = Validation.byDefaultProvider().configure();
			ValidatorFactory factory = config.buildValidatorFactory();
			validator = factory.getValidator();
			factory.close();
		}
		return validator;
	}

}
