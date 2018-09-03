package bdn.quantum.model;

import java.util.List;

//
// Position is a rough representation of SecurityEntity and underlying TranEntities
//
public class Position {

	private Integer secId;
	private String symbol;
	private Double principal;
	private Double shares;
	private Double realizedProfit;		// profit/loss realized from sales of security or dividends
	private Double price;
	private List<TranEntity> transactions;

	public Position() {
	}

	public Position(Integer secId, String symbol, Double principal, Double shares, Double realizedProfit, Double price,
			List<TranEntity> transactions) {
		this.secId = secId;
		this.symbol = symbol;
		this.principal = principal;
		this.shares = shares;
		this.realizedProfit = realizedProfit;
		this.price = price;
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

	public Double getPrincipal() {
		return principal;
	}

	public void setPrincipal(Double principal) {
		this.principal = principal;
	}

	public Double getShares() {
		return shares;
	}

	public void setShares(Double shares) {
		this.shares = shares;
	}

	public Double getRealizedProfit() {
		return realizedProfit;
	}

	public void setRealizedProfit(Double realizedProfit) {
		this.realizedProfit = realizedProfit;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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
