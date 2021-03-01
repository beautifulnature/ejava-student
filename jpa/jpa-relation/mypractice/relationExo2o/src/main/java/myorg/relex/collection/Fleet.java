package myorg.relex.collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class provides an example one/parent entity with a relationship to many
 * child/dependent objects -- with the members in each collection based on a
 * different hashCode/equals method.
 */

@Entity
@Table(name = "RELATIONEX_FLEET")
public class Fleet {

	@Id
	@GeneratedValue
	private int id;

	@Column(length = 16)
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}