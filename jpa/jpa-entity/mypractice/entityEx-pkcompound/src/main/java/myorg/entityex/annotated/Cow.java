package myorg.entityex.annotated;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ENITYEX_COW")
public class Cow {
	
	@EmbeddedId
	@AttributeOverrides(
			@AttributeOverride(name="name", column = @Column(name="NAME", length=16))
	)
	private CowPK pk;
	
	private int weight;
	
	public Cow() {
		super();
	}
	
	public Cow(CowPK pk) {
		super();
		this.pk = pk;
	}

	public CowPK getPk() {
		return pk;
	}

	public void setPk(CowPK pk) {
		this.pk = pk;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
