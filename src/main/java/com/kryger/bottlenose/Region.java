package com.kryger.bottlenose;

public enum Region {
	CA("ecs.amazonaws.ca"), CN("webservices.amazon.cn"), DE("ecs.amazonaws.de"), ES(
			"webservices.amazon.es"), FR("ecs.amazonaws.fr"), IN(
			"webservices.amazon.in"), IT("webservices.amazon.it"), JP(
			"ecs.amazonaws.jp"), UK("ecs.amazonaws.co.uk"), US(
			"ecs.amazonaws.com");

	private final String url;

	private Region(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
