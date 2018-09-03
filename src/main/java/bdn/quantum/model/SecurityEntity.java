package bdn.quantum.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SecurityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer secId;
	private Integer basketId;
	private String symbol;
	
	public SecurityEntity() {}
	
	public SecurityEntity(Integer secId, Integer basketId, String symbol) {
		this.secId = secId;
		this.basketId = basketId;
		this.symbol = symbol;
	}

	public Integer getSecId() {
		return secId;
	}

	public void setSecId(Integer secId) {
		this.secId = secId;
	}

	public Integer getBasketId() {
		return basketId;
	}

	public void setBasketId(Integer basketId) {
		this.basketId = basketId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SecId:").append(secId);
		strBuf.append(" BasketId:").append(basketId);
		strBuf.append(" Symbol:").append(symbol);
		return strBuf.toString();
	}
}
