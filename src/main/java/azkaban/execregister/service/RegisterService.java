package azkaban.execregister.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import azkaban.execregister.model.AzkExecutor;
import azkaban.execregister.repository.AzkabanRepository;
import azkaban.execregister.repository.EtcdRepository;

@Service
public class RegisterService {

	private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
	
	@Autowired
	private EtcdRepository etcdRepo;
	@Autowired
	private AzkabanRepository azkRepo;
	@Autowired
	private AzkabanRestService azkRestService;
	
	private boolean needSync = false;
	
	@Scheduled(fixedDelayString="${register.interval}")
	public void register() throws Exception {

		logger.debug("start register");

		List<AzkExecutor> syncExecList = new ArrayList<AzkExecutor>();

		logger.debug("try get all executors from ETCD...");
		List<AzkExecutor> etcdExecList = etcdRepo.getAllExecutors();
		logger.debug("try get all executors from DB...");
		List<AzkExecutor> azkExecList = azkRepo.selectAllExecutors();
		Map<String,AzkExecutor> azkExecMap = new HashMap<String, AzkExecutor>();
		for(AzkExecutor exec: azkExecList) {
			azkExecMap.put(exec.getKey(), exec);
		}
		
		for(AzkExecutor etcdExec: etcdExecList) {
			logger.debug("check node: " + etcdExec.getKey());
			AzkExecutor azkExec = azkExecMap.remove(etcdExec.getKey());
			if(azkExec != null) {
				if(!azkExec.getActive()) {
					azkExec.setActive(true);
					syncExecList.add(azkExec);
				}
			}
			else {
				syncExecList.add(etcdExec);
			}
		}
		for(AzkExecutor azkExec: azkExecMap.values()) {
			if(azkExec.getActive()) {
				azkExec.setActive(false);
				syncExecList.add(azkExec);
			}
		}
		
		
		for(AzkExecutor syncExec: syncExecList) {
			logger.info("syncExec: " + syncExec.getKey() + "-" + syncExec.getActive());
			if(syncExec.getId() != null) {
				azkRepo.updateExecutor(syncExec);
			}
			else {
				azkRepo.insertExecutor(syncExec);
			}
		}

		if(syncExecList.size() > 0) {
			needSync = true;
		}
		if(needSync) {
			logger.info("send reload");
			try {
				azkRestService.sendReloadExecutors();
				needSync= false;
			} catch (Exception e) {
				logger.error("fail to sendReloadExecutors", e);
			}
		}
	}
}
