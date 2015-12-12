package com.kryger.bottlenose;

public enum Operation {
	ITEM_LOOKUP("ItemLookup"), ITEM_SEARCH("ItemSearch");

	private final String amazonOperation;

	Operation(String amazonOperation) {
		this.amazonOperation = amazonOperation;
	}

	public String getOperationToken() {
		return amazonOperation;
	};
}
