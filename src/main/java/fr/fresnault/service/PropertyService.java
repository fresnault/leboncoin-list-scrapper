package fr.fresnault.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.fresnault.config.LeBonCoinConfig;
import fr.fresnault.domain.Property;

@Service
public class PropertyService {

	private final LeBonCoinConfig leBonCoinConfig;

	public PropertyService(LeBonCoinConfig leBonCoinConfig) {
		this.leBonCoinConfig = leBonCoinConfig;
	}

	@Async
	public void scrapProperty(Property savedProperty) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + leBonCoinConfig.getIdToken());
		HttpEntity<Property> entity = new HttpEntity<Property>(savedProperty, headers);
		restTemplate.put("http://localhost:8082/leboncoinDetailScrapper/api/properties/", entity);
	}

}
