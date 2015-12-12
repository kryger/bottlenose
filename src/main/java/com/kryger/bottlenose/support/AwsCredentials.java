package com.kryger.bottlenose.support;

public class AwsCredentials {
	private final String awsAccessKey;
	private final String awsAssociateTag;
	private final String awsSecretAccessKey;

	public AwsCredentials(String awsAccessKey, String awsAssociateTag,
			String awsSecretAccessKey) {
		this.awsAccessKey = awsAccessKey;
		this.awsAssociateTag = awsAssociateTag;
		this.awsSecretAccessKey = awsSecretAccessKey;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public String getAwsAssociateTag() {
		return awsAssociateTag;
	}

	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}

}
