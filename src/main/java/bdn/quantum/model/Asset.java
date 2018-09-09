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
	private BigDecimal realizedProfit;
	private List<Position> positions;

	public Asset() {
	}

	public Asset(Integer basketId, String basketName, BigDecimal principal, BigDecimal lastValue,
			BigDecimal realizedProfit, List<Position> positions) {
		this.basketId = basketId;
		this.basketName = basketName;
		this.principal = principal;
		this.lastValue = lastValue;
		this.realizedProfit = realizedProfit;
		this.positions = positions;
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

	public BigDecimal getRealizedProfit() {
		return realizedProfit;
	}

	public void setRealizedProfit(BigDecimal realizedProfit) {
		this.realizedProfit = realizedProfit;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
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
