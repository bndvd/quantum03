package bdn.quantum.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//
// Position is a rough representation of SecurityEntity and underlying TranEntities
//
public class Position {

	public static final Position EMPTY_POSITION = new Position(0, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new ArrayList<TranEntity>());
	
	private Integer secId;
	private String symbol;
	private BigDecimal principal;
	private BigDecimal shares;
	private BigDecimal realizedProfit; // profit/loss realized from sales of security or dividends
	private BigDecimal lastPrice;
	private List<TranEntity> transactions;

	public Position() {
	}

	public Position(Integer secId, String symbol, BigDecimal principal, BigDecimal shares, BigDecimal realizedProfit,
			BigDecimal lastPrice, List<TranEntity> transactions) {
		this.secId = secId;
		this.symbol = symbol;
		this.principal = principal;
		this.shares = shares;
		this.realizedProfit = realizedProfit;
		this.lastPrice = lastPrice;
		this.transactions = transactions;
	}

	public Integer getSecId() {
		return secId;
	}

	public void setSecId(Integer secId) {
		this.secId = secId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getShares() {
		return shares;
	}

	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}

	public BigDecimal getRealizedProfit() {
		return realizedProfit;
	}

	public void setRealizedProfit(BigDecimal realizedProfit) {
		this.realizedProfit = realizedProfit;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal price) {
		this.lastPrice = price;
	}

	public List<TranEntity> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TranEntity> transactions) {
		this.transactions = transactions;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SecId:");
		strBuf.append(secId);
		strBuf.append(", Symbol:");
		strBuf.append(symbol);
		return strBuf.toString();
	}

}
