package myorg.entityex.mapped;

import java.math.BigDecimal;
import java.util.Date;

public class Cat {
	
	private int id;
    private String name;
    private Date dob;
    private BigDecimal weight;
    
    public Cat() {
		super();
	}

	public Cat(String name, Date dob, BigDecimal weight) {
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

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}