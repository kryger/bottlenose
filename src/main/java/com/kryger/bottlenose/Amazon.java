package com.kryger.bottlenose;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;
import com.kryger.bottlenose.support.AwsCredentials;
import com.kryger.bottlenose.support.UrlUtils;

public class Amazon {
	private final AwsCredentials awsCredentials;
	private final ExecutorService executor = Executors.newFixedThreadPool(2);
	private CloseableHttpClient httpClient;

	private final Region region;

	public Amazon(String awsAccessKey, String awsSecretAccessKey,
			String awsAssociateTag, Region region) {
		this.awsCredentials = new AwsCredentials(awsAccessKey, awsAssociateTag,
				awsSecretAccessKey);
		this.region = region;
		this.httpClient = HttpClients.createDefault();
	}

	public Amazon(String awsAccessKey, String awsSecretAccessKey,
			String awsAssociateTag, Region region,
			CloseableHttpClient httpClient) {
		this.awsCredentials = new AwsCredentials(awsAccessKey, awsAssociateTag,
				awsSecretAccessKey);
		this.region = region;
		this.httpClient = httpClient;
	}

	private <T> Callable<T> callServiceAndUnmarshal(HttpGet httpGet,
			Class<T> targetClass) {
		return () -> {
			CloseableHttpResponse response = httpClient.execute(httpGet);

			try {
				InputStream responseStream = response.getEntity().getContent();
				JAXBContext context = JAXBContext.newInstance(targetClass);
				Unmarshaller unMarshaller = context.createUnmarshaller();
				@SuppressWarnings("unchecked")
				T itemLookupResponse = (T) unMarshaller
						.unmarshal(responseStream);

				return itemLookupResponse;
			} finally {
				response.close();
			}
		};
	}

	/**
	 * http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemLookup.html
	 *
	 * @param extraParams
	 *            an even number of String combined into key-value pairs to be
	 *            sent to the service
	 * @return
	 */
	public Future<ItemLookupResponse> itemLookup(String... extraParams) {
		HttpGet httpGet = prepareAmazonGet(Operation.ITEM_LOOKUP, extraParams);

		Callable<?> job = callServiceAndUnmarshal(httpGet,
				ItemLookupResponse.class);
		@SuppressWarnings("unchecked")
		Future<ItemLookupResponse> responseFuture = (Future<ItemLookupResponse>) executor
				.submit(job);
		return responseFuture;
	}

	/**
	 * http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemSearch.html
	 *
	 * @param extraParams
	 *            an even number of String combined into key-value pairs to be
	 *            sent to the service
	 * @return
	 */
	public Future<ItemSearchResponse> itemSearch(String... extraParams) {

		HttpGet httpGet = prepareAmazonGet(Operation.ITEM_SEARCH, extraParams);

		Callable<?> job = callServiceAndUnmarshal(httpGet,
				ItemSearchResponse.class);
		@SuppressWarnings("unchecked")
		Future<ItemSearchResponse> responseFuture = (Future<ItemSearchResponse>) executor
				.submit(job);
		return responseFuture;
	}

	private HttpGet prepareAmazonGet(Operation operation, String... extraParams) {
		String url = UrlUtils.makeUrlString(operation, this.region,
				this.awsCredentials, extraParams);
		HttpGet httpGet = new HttpGet(url);
		return httpGet;
	}

}