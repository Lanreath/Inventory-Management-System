package com.ils.logic;

import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;

public class Logic {
	private static Filters filters;
	private static DataSync dataSync;
	private static CustomerManagement customerManagement;
	private static ProductManagement productManagement;
	private static PartManagement partManagement;
	private static TransferManagement transferManagement;

	static {
		filters = new Filters();
		dataSync = new DataSync();
		customerManagement = new CustomerManagement(filters);
		productManagement = new ProductManagement(filters);
		partManagement = new PartManagement(filters);
		transferManagement = new TransferManagement(filters);
	}

	public static CustomerManagement getCustomerManagement() {
		return customerManagement;
	}

	public static ProductManagement getProductManagement() {
		return productManagement;
	}

	public static PartManagement getPartManagement() {
		return partManagement;
	}

	public static TransferManagement getTransferManagement() {
		return transferManagement;
	}

	public static Filters getFilters() {
		return filters;
	}

	public static DataSync getDataSync() {
		return dataSync;
	}

}