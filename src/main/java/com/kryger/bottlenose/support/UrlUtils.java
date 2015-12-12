package com.kryger.bottlenose.support;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.ArrayUtils;

import com.kryger.bottlenose.Operation;
import com.kryger.bottlenose.Region;

public class UrlUtils {
	/*-
	 * Adapted from
	 *
	 * http://www.jokecamp.com/blog/examples-of-creating-base64-hashes-using-hmac-sha256-in-different-languages/#java
	 *
	 */
	public static String makeSignature(String paramsPart, Region region,
			String awsSecretAccessKey) {
		String data = "GET\n" + region.getUrl() + "\n/onca/xml\n" + paramsPart;

		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(
					awsSecretAccessKey.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(data
					.getBytes()));
			return hash;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	static String makeUrl(Object... params) {
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException(
					String.format(
							"List of input paramter key/value must be even to allow pairing. Was: %s",
							Arrays.toString(params)));
		}
		List<Tuple> kvPairs = new ArrayList<Tuple>();

		for (int i = 0; i < params.length; i += 2) {
			String key = params[i].toString();
			String value = safeEncode(params[i + 1].toString());
			Tuple t = new Tuple(key, value);
			kvPairs.add(t);
		}
		Collections.sort(kvPairs, (e1, e2) -> e1.key.compareTo(e2.key));

		String result = kvPairs.stream().map(e1 -> e1.key + "=" + e1.value)
				.collect(Collectors.joining("&"));
		return result;
	}

	public static String makeUrlString(Operation operation, Region region,
			AwsCredentials awsCredentials, String... extraParams) {
		String version = "2011-08-01";

		String baseUrl = String.format("http://%s/onca/xml?", region.getUrl());

		Object[] params = new Object[] { "AWSAccessKeyId",
				awsCredentials.getAwsAccessKey(), "AssociateTag",
				awsCredentials.getAwsAssociateTag(), "Operation",
				operation.getOperationToken(), "Service",
				"AWSECommerceService", "Timestamp", Instant.now(), "Version",
				version };
		String paramsJoined = makeUrl(ArrayUtils.addAll(params,
				(Object[]) extraParams));
		final String requestSignature = makeSignature(paramsJoined, region,
				awsCredentials.getAwsSecretAccessKey());
		String url = baseUrl + paramsJoined + "&Signature="
				+ safeEncode(requestSignature);
		return url;
	}

	private static String safeEncode(String in) {
		try {
			return new URLCodec().encode(in);
		} catch (EncoderException e) {
			throw new RuntimeException(e);
		}
	}

}
