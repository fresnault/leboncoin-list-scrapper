package fr.fresnault.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fresnault.web.rest.vm.LoginVM;

@Component
public class LeBonCoinConfig {

	private Map<String, String> headers = new HashMap<>();
	private List<String> localisationPages = new ArrayList<>();
	private String idToken = null;

	public List<String> getLocalisationPages() {

		if (localisationPages.isEmpty()) {
			for (String name : Arrays.asList("alsace/bas_rhin", "alsace/haut_rhin", "aquitaine/dordogne",
					"aquitaine/gironde", "aquitaine/landes", "aquitaine/lot_et_garonne",
					"aquitaine/pyrenees_atlantiques", "auvergne/allier", "auvergne/cantal", "auvergne/haute_loire",
					"auvergne/puy_de_dome", "basse_normandie/calvados", "basse_normandie/manche",
					"basse_normandie/orne", "bourgogne/cote_d_or", "bourgogne/nievre", "bourgogne/saone_et_loire",
					"bourgogne/yonne", "bretagne/cotes_d_armor", "bretagne/finistere", "bretagne/ille_et_vilaine",
					"bretagne/morbihan", "centre/cher", "centre/eure_et_loir", "centre/indre", "centre/indre_et_loire",
					"centre/loir_et_cher", "centre/loiret", "champagne_ardenne/ardennes", "champagne_ardenne/aube",
					"champagne_ardenne/marne", "champagne_ardenne/haute_marne", "corse", "franche_comte/doubs",
					"franche_comte/jura", "franche_comte/haute_saone", "franche_comte/territoire_de_belfort",
					"haute_normandie/eure", "haute_normandie/seine_maritime", "ile_de_france/paris",
					"ile_de_france/seine_et_marne", "ile_de_france/yvelines", "ile_de_france/essonne",
					"ile_de_france/hauts_de_seine", "ile_de_france/seine_saint_denis", "ile_de_france/val_de_marne",
					"ile_de_france/val_d_oise", "languedoc_roussillon/aude", "languedoc_roussillon/gard",
					"languedoc_roussillon/herault", "languedoc_roussillon/lozere",
					"languedoc_roussillon/pyrenees_orientales", "limousin/correze", "limousin/creuse",
					"limousin/haute_vienne", "lorraine/meurthe_et_moselle", "lorraine/meuse", "lorraine/moselle",
					"lorraine/vosges", "midi_pyrenees/ariege", "midi_pyrenees/aveyron", "midi_pyrenees/haute_garonne",
					"midi_pyrenees/gers", "midi_pyrenees/lot", "midi_pyrenees/hautes_pyrenees", "midi_pyrenees/tarn",
					"midi_pyrenees/tarn_et_garonne", "nord_pas_de_calais/nord", "nord_pas_de_calais/pas_de_calais",
					"pays_de_la_loire/loire_atlantique", "pays_de_la_loire/maine_et_loire", "pays_de_la_loire/mayenne",
					"pays_de_la_loire/sarthe", "pays_de_la_loire/vendee", "picardie/aisne", "picardie/oise",
					"picardie/somme", "poitou_charentes/charente", "poitou_charentes/charente_maritime",
					"poitou_charentes/deux_sevres", "poitou_charentes/vienne",
					"provence_alpes_cote_d_azur/alpes_de_haute_provence", "provence_alpes_cote_d_azur/hautes_alpes",
					"provence_alpes_cote_d_azur/alpes_maritimes", "provence_alpes_cote_d_azur/bouches_du_rhone",
					"provence_alpes_cote_d_azur/var", "provence_alpes_cote_d_azur/vaucluse", "rhone_alpes/ain",
					"rhone_alpes/ardeche", "rhone_alpes/drome", "rhone_alpes/isere", "rhone_alpes/loire",
					"rhone_alpes/rhone", "rhone_alpes/savoie", "rhone_alpes/haute_savoie", "martinique", "guyane",
					"reunion")) {
				localisationPages.add(
						"https://www.leboncoin.fr/ventes_immobilieres/offres/[LIEU]/p-[PAGE]/".replace("[LIEU]", name));
				localisationPages
						.add("https://www.leboncoin.fr/locations/offres/[LIEU]/p-[PAGE]/".replace("[LIEU]", name));
			}
		}
		return localisationPages;
	}

	public Map<String, String> getHeaders() {
		if (headers.isEmpty()) {
			headers.put("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			headers.put("Accept-Encoding", "gzip, deflate, br");
			headers.put("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
			headers.put("Connection", "keep-alive");
			headers.put("Host", "www.leboncoin.fr");
			headers.put("Upgrade-Insecure-Requests", "1");
			headers.put("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

		}
		return headers;
	}

	public String getIdToken() {
		if (idToken == null) {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			LoginVM user = new LoginVM();
			user.setUsername("admin");
			user.setPassword("admin");
			user.setRememberMe(true);
			HttpEntity<LoginVM> entity = new HttpEntity<LoginVM>(user, headers);
			JWTToken response = restTemplate.postForObject(
					"http://localhost:8082/leboncoinDetailScrapper/api/authenticate", entity, JWTToken.class);
			idToken = response.getIdToken();
		}
		return idToken;
	}

	public Integer getNbPages(Document document) {
		return (Integer.valueOf(document.select("meta[name=description]").attr("content").replaceAll("[^0-9]", ""))
				/ 35) + 1;
	}

	public Set<String> getPropertiesLink(Document document) {
		return new HashSet<>(document.select("li[data-qa-id=aditem_container] > a").eachAttr("href")).stream()
				.map(url -> "http://www.leboncoin.fr" + url.substring(0, url.length() - 1)).collect(Collectors.toSet());
	}

	@JsonSerialize
	static class JWTToken {

		private String idToken;

		public JWTToken() {
			super();
		}

		JWTToken(String idToken) {
			this.idToken = idToken;
		}

		@JsonProperty("id_token")
		String getIdToken() {
			return idToken;
		}

		void setIdToken(String idToken) {
			this.idToken = idToken;
		}
	}

}