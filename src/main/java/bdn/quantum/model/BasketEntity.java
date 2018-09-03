package bdn.quantum.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BasketEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer basketId;
	private String name;

	public BasketEntity() {}
	
	public BasketEntity(Integer basketId, String name) {
		this.basketId = basketId;
		this.name = name;
	}
	
	public Integer getBasketId() {
		return basketId;
	}

	public void setBasketId(Integer basketId) {
		this.basketId = basketId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("BasketId:").append(basketId);
		strBuf.append(" Name:").append(name);
		return strBuf.toString();
	}

}
