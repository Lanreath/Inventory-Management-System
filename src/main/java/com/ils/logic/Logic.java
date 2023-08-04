package com.ils.logic;

import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;

public class Logic {
	private static Filters filters;
	private static CustomerManagement customerManagement;
	private static ProductManagement productManagement;
	private static PartManagement partManagement;
	private static TransferManagement transferManagement;

	static {
		// Initialize the logic.
		filters = new Filters();
		customerManagement = new CustomerManagement(filters);
		productManagement = new ProductManagement(filters);
		partManagement = new PartManagement(filters);
		transferManagement = new TransferManagement(filters);
	}

	/**
	 * Get the customer management.
	 * @return CustomerManagement
	 */
	public static CustomerManagement getCustomerManagement() {
		return customerManagement;
	}

	/**
	 * Get the product management.
	 * @return ProductManagement
	 */
	public static ProductManagement getProductManagement() {
		return productManagement;
	}

	/**
	 * Get the part management.
	 * @return PartManagement
	 */
	public static PartManagement getPartManagement() {
		return partManagement;
	}

	/**
	 * Get the transfer management.
	 * @return TransferManagement
	 */
	public static TransferManagement getTransferManagement() {
		return transferManagement;
	}

	/**
	 * Get the filters.
	 * @return Filters
	 */
	public static Filters getFilters() {
		return filters;
	}
}