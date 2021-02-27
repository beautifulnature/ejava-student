package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="RELATIONEX_LICENSE")
public class License {
	
	@Id
    @GeneratedValue
    private int id;

    @Temporal(TemporalType.DATE)
    private Date renewal;

    public int getId() { return id; }

    public Date getRenewal() { return renewal; }
    public void setRenewal(Date renewal) {
        this.renewal = renewal;
    }
}
