package bdn.quantum.model;

import java.math.BigDecimal;
import java.util.List;

//
// Asset is a rough representation of a Basket, and is a grouping of one or more Positions that 
// represent the same underlying asset (e.g., an index fund and an ETF of the same asset)
//
public class Asset {

	private Integer basketId;
	private String basketName;
	private BigDecimal principal;
	private BigDecimal lastValue;
	private BigDecimal realizedGain;

	public Asset() {
	}

	public Asset(Integer basketId, String basketName, BigDecimal principal, BigDecimal lastValue,
			BigDecimal realizedGain) {
		this.basketId = basketId;
		this.basketName = basketName;
		this.principal = principal;
		this.lastValue = lastValue;
		this.realizedGain = realizedGain;
	}

	public Integer getBasketId() {
		return basketId;
	}

	public void setBasketId(Integer basketId) {
		this.basketId = basketId;
	}

	public String getBasketName() {
		return basketName;
	}

	public void setBasketName(String basketName) {
		this.basketName = basketName;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getLastValue() {
		return lastValue;
	}

	public void setLastValue(BigDecimal lastValue) {
		this.lastValue = lastValue;
	}

	public BigDecimal getRealizedGain() {
		return realizedGain;
	}

	public void setRealizedGain(BigDecimal realizedGain) {
		this.realizedGain = realizedGain;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("BasketId:");
		strBuf.append(basketId);
		strBuf.append(", BasketName:");
		strBuf.append(basketName);
		return strBuf.toString();
	}

}
