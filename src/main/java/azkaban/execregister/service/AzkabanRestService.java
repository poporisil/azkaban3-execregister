package azkaban.execregister.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class AzkabanRestService {
	
	@Value("${azkaban.rest.url}")
	private String azkUrl;
	
	@Value("${azkaban.rest.username}")
	private String azkUsername;

	@Value("${azkaban.rest.password}")
	private String azkPassword;
	
	@Autowired
	RestTemplate restTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();

	private String getSessionId() {
		String sessionId = null;
		MultiValueMap<String, String> login = new LinkedMultiValueMap<String, String>();
		login.add("action", "login");
		login.add("username", azkUsername);
		login.add("password", azkPassword);
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(azkUrl, login, String.class);
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			String response = responseEntity.getBody();
			if(response != null) {
				Map<String, String> map = new HashMap<String, String>();
				try {
					map = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
					sessionId = map.get("session.id");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return sessionId;
	}
	
	public void sendReloadExecutors() throws Exception {
		String sessionId = getSessionId();
		if(sessionId == null) {
			throw new Exception("Fail to get session.id from azkaban");
		}
		
		String url = azkUrl + "/executor?ajax=reloadExecutors&session.id=" + sessionId;
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
		if(responseEntity.getStatusCode() != HttpStatus.OK) {
			throw new Exception("Fail to reloadExecutors");
		}
	}

}
