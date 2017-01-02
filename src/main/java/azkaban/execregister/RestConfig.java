package azkaban.execregister;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

	private static final int DEFAULT_READ_TIMEOUT_MSEC = 10000;
	private static final int DEFAULT_CONNECTION_TIMEOUT_MSEC = 10000;
	
	@Bean
	public RestTemplate restTemplate(SimpleClientHttpRequestFactory simpleClientHttpRequestFactory) {
		return new RestTemplate(simpleClientHttpRequestFactory);
	}

	@Bean
	public SimpleClientHttpRequestFactory SimpleClientRequestFactory() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT_MSEC);
		requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT_MSEC);

		if(System.getProperty("http.proxyHost") != null) {
			String proxyHost = System.getProperty("http.proxyHost");
			Integer proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
			Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress( proxyHost, proxyPort));
			requestFactory.setProxy(proxy);
		}

		return requestFactory;
	}

}
