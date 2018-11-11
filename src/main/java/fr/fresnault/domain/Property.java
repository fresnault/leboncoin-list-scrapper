package fr.fresnault.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fr.fresnault.domain.enumeration.Source;

/**
 * A Property.
 */
@Document(collection = "property")
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@NotNull
	@Field("ref_source")
	private Source refSource;

	@NotNull
	@Field("ref_id")
	private String refId;

	@NotNull
	@Field("url")
	private String url;

	// jhipster-needle-entity-add-field - JHipster will add fields here, do not
	// remove
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Source getRefSource() {
		return refSource;
	}

	public Property refSource(Source refSource) {
		this.refSource = refSource;
		return this;
	}

	public void setRefSource(Source refSource) {
		this.refSource = refSource;
	}

	public String getRefId() {
		return refId;
	}

	public Property refId(String refId) {
		this.refId = refId;
		return this;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getUrl() {
		return url;
	}

	public Property url(String url) {
		this.url = url;
		return this;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	// jhipster-needle-entity-add-getters-setters - JHipster will add getters
	// and setters here, do not remove

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Property property = (Property) o;
		if (property.getId() == null || getId() == null) {
			return false;
		}
		return Objects.equals(getId(), property.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

}
