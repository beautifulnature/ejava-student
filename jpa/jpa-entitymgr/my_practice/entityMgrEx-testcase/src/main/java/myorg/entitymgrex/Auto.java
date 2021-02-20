package myorg.entitymgrex;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EM_AUTO")
public class Auto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String make;
	private String model;
	private String color;
	private int mileage;

	public Auto(){
	}
	
	public Auto(int id){
		this.id = id;		
	}

	public long getId() {
		return id;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	@Override
	public String toString() {
		return "Auto [id=" + id 
				+ ", make=" + make 
				+ ", model=" + model 
				+ ", color=" + color 
				+ ", mileage=" + mileage
				+ "]";
	}
}
