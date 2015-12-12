package com.kryger.bottlenose;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mockito;

public class AmazonTest {
	final static String AWS_ACCESS_KEY_ID = "access-key";
	final static String AWS_ASSOCIATE_TAG = "de";
	final static String AWS_SECRET_ACCESS_KEY = "secret-key";

	/**
	 * Set up a mock implementation of CloseableHttpClient that returns a canned
	 * XML response read from file.
	 */
	private CloseableHttpClient createMockHttpClient(String fileToServe)
			throws IOException {
		CloseableHttpClient mockHttpClient = Mockito
				.mock(CloseableHttpClient.class);
		CloseableHttpResponse mockResponse = Mockito.mock(
				CloseableHttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		InputStream realFileInputStream = FileUtils.openInputStream(new File(
				fileToServe));
		Mockito.when(mockResponse.getEntity().getContent()).thenReturn(
				realFileInputStream);
		// Amazon request includes a Signature hash that takes in the timestamp
		// TODO figure out how to isolate to allow more strict testing
		Mockito.when(mockHttpClient.execute(Mockito.any(HttpGet.class)))
				.thenReturn(mockResponse);
		return mockHttpClient;
	}

	@Test
	public void testItemLookup() throws InterruptedException,
			ExecutionException, IOException {
		CloseableHttpClient mockHttpClient = createMockHttpClient("src/test/resources/ItemLookupResponse.xml");

		Amazon amazon = new Amazon(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY,
				AWS_ASSOCIATE_TAG, Region.UK, mockHttpClient);

		// this returns a canned response, only verifying no exception was
		// thrown as of now
		amazon.itemLookup("ItemId", "B000006045").get();
	}

	@Test
	public void testItemSearch() throws InterruptedException,
			ExecutionException, IOException {

		CloseableHttpClient mockHttpClient = createMockHttpClient("src/test/resources/ItemSearchResponse.xml");

		Amazon amazon = new Amazon(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY,
				AWS_ASSOCIATE_TAG, Region.UK, mockHttpClient);

		// this returns a canned response, only verifying no exception was
		// thrown as of now
		amazon.itemSearch("SearchIndex", "Music", "Keywords", "Mezzanine")
				.get();
	}

}
