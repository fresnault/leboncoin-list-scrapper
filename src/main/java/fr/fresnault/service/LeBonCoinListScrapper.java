package fr.fresnault.service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.IntStream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.fresnault.config.LeBonCoinConfig;
import fr.fresnault.domain.Property;
import fr.fresnault.domain.enumeration.Source;
import fr.fresnault.repository.PropertyRepository;

@Component
public class LeBonCoinListScrapper {

	private final Logger log = LoggerFactory.getLogger(LeBonCoinListScrapper.class);

	private final PropertyRepository propertyRepository;

	private final PropertyService propertyService;

	private final LeBonCoinConfig leBonCoinConfig;

	public LeBonCoinListScrapper(PropertyRepository propertyRepository, PropertyService propertyService,
			LeBonCoinConfig leBonCoinConfig) {
		this.propertyRepository = propertyRepository;
		this.propertyService = propertyService;
		this.leBonCoinConfig = leBonCoinConfig;
	}

	@Scheduled(fixedDelay = 1800000) // 30 minutes
	public void run() throws IOException, InterruptedException {
		for (String localisationPages : leBonCoinConfig.getLocalisationPages()) {
			Deque<Boolean> deque = new ArrayDeque<>();

			String firstPageLink = replacePageInUrl(localisationPages, 1);

			Document docFirstPage = getDocument(firstPageLink);

			Integer nbPages = leBonCoinConfig.getNbPages(docFirstPage);
			log.info("{} pages dans {}", nbPages, firstPageLink);

			IntStream.range(1, nbPages).allMatch(pageIndex -> {
				if (deque.size() > 100) {
					if (!deque.isEmpty() && deque.stream().allMatch(e -> Boolean.TRUE == e)) {
						log.info("Les biens immobiliers ont déjà été traités");
						return false;
					}
					deque.clear();
				}

				try {
					String currentPageLink = replacePageInUrl(localisationPages, pageIndex);

					Document document = getDocument(currentPageLink);

					// Récupération des liens des biens immobiliers présents
					// dans le document
					Set<String> propertiesLink = leBonCoinConfig.getPropertiesLink(document);
					log.info("{} liens dans {}", propertiesLink.size(), currentPageLink);

					if (propertiesLink.size() == 0) {
						return false;
					}

					// On envoie l'ensemble des liens dans RabbitMQ
					for (String url : propertiesLink) {
						if (propertyRepository.findById(url).isPresent()) {
							log.info("La référence {} existe déjà dans la base", url);
							deque.push(true);
						} else {
							log.info("La référence {} n'existe pas dans la base", url);
							deque.push(false);

							int beginIndex = url.lastIndexOf('/');
							int endIndex = url.lastIndexOf('.');
							String refId = url.substring(beginIndex + 1, endIndex);

							Property property = new Property();
							property.setRefSource(Source.LEBONCOIN);
							property.setRefId(refId);
							property.setUrl(url);

							Property savedProperty = propertyRepository.save(property);
							propertyService.scrapProperty(savedProperty);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			});
		}

	}

	private Document getDocument(String url) throws InterruptedException {
		Boolean connected = false;
		int nbTry = 0;
		Connection connexion = null;
		Document document = null;

		while (!connected && nbTry++ < 30) {
			try {
				connexion = Jsoup.connect(url).headers(leBonCoinConfig.getHeaders()).followRedirects(true);
				document = connexion.get();
				connected = true;
			} catch (Exception e) {
				log.error("Connect failed, retry " + nbTry + "...");
				Thread.sleep(1000 * nbTry);
			}
		}
		if (document == null) {
			throw new IllegalStateException("Impossible to connect to " + url);
		}
		return document;
	}

	private String replacePageInUrl(String localisationPages, int currentPage) {
		return localisationPages.replace("[PAGE]", String.valueOf(currentPage));
	}

}