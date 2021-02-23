package myorg.entityex.annotated;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@javax.persistence.Entity
@javax.persistence.Table(name="ENTITYEX_CAT")
public class Cat2 {
	
	private static final Logger logger = LoggerFactory.getLogger(Cat2.class);
	
	@javax.persistence.Id
	@javax.persistence.Column(name="CAT_ID")
	private int id;
	@javax.persistence.Column(nullable=false, length=20)
    private String name;
    private Date dob;
    private double weight;
    
    public Cat2() {
		super();
	}

	public Cat2(String name, Date dob, double weight) {
        this.name = name;
        this.dob = dob;
        this.weight = weight;
    }

    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() { return dob; }
    public void setDob(Date dob) {
        this.dob = dob;
    }

    public BigDecimal getWeight() { 
    	logger.debug("annotated.getWeight()");
    	return new BigDecimal(weight);
    }
    public void setWeight(BigDecimal weight) {
    	logger.debug("annotated.setWeight()");
        this.weight = weight == null ? 0 : weight.doubleValue();
    }
}