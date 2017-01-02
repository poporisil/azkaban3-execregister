package azkaban.execregister.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import azkaban.execregister.model.AzkExecutor;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode;

@Repository
public class EtcdRepository {

	@Value("${etcd.executorKey}")
	private String etcdExecutorKey;

	@Autowired
	private EtcdClient client;

	public List<AzkExecutor> getAllExecutors() throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {
		EtcdKeysResponse resp = client.get(etcdExecutorKey).send().get();
		
		List<AzkExecutor> execList = new ArrayList<AzkExecutor>();
		if(resp.getNode().isDir()) {
			for(EtcdNode node: resp.getNode().getNodes()) {
				String nodeName = node.getKey().substring(etcdExecutorKey.length()+1);
				if(!nodeName.equals("port")) {
					String[] value = node.getValue().split("\\:");
					if(value.length==2) {
						AzkExecutor exec = new AzkExecutor();
						exec.setHost(value[0]);
						exec.setPort(Integer.parseInt(value[1]));
						exec.setActive(true);
						execList.add(exec);
					}
				}
			}
		}
		return execList;
	}
}
