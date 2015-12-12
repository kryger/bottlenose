package com.kryger.bottlenose.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.kryger.bottlenose.Region;

public class UrlUtilsTest {

	@Test
	public void testHash() {
		String in = "some-input";
		String AWS_SECRET_ACCESS_KEY = "some-secret-access-key";
		String expected = "eeaTOn2QbYQIr9354CURXX/8JgqKJ4VxyxZeuTVrAcw=";

		assertThat(UrlUtils.makeSignature(in, Region.UK, AWS_SECRET_ACCESS_KEY))
				.isEqualTo(expected);
	}

}
