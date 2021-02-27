package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This entity class provides an example of cascades being originated from the
 * inverse side of a bi-directional relationship.
 */
@Entity
@Table(name = "RELATIONEX_TICKET")
public class Ticket {

	@Id
	@GeneratedValue
	private int id;

	@OneToOne(mappedBy = "ticket", fetch = FetchType.EAGER, cascade = {
			 CascadeType.PERSIST
			 ,CascadeType.DETACH
			 ,CascadeType.REFRESH
			 ,CascadeType.MERGE
			 ,CascadeType.REMOVE
	})
	private Passenger passenger;

	@Temporal(TemporalType.DATE)
	Date date;

	public Ticket() {
	}

	public Ticket(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}