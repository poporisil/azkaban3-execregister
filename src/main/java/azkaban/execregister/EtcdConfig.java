package azkaban.execregister;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mousio.etcd4j.EtcdClient;

@Configuration
public class EtcdConfig {
	
	@Value("${etcd.url}")
	private String etcdUrl;
	
	@Bean
	public EtcdClient etcdClient() {
		return new EtcdClient(URI.create(etcdUrl));
	}

}
