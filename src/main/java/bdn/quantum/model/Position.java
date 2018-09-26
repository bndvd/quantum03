package bdn.quantum.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bdn.quantum.QuantumConstants;

//
// Position is a rough representation of Security and underlying Transactions
//
public class Position {

	public static final Position EMPTY_POSITION = new Position(0, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new ArrayList<Transaction>());
	
	private Integer secId;
	private String symbol;
	private BigDecimal principal;
	private BigDecimal shares;
	private BigDecimal realizedGain; // profit/loss realized from sales of security or dividends
	private BigDecimal lastPrice;
	private List<Transaction> transactions;

	public Position() {
	}

	public Position(Integer secId, String symbol, BigDecimal principal, BigDecimal shares, BigDecimal realizedGain,
			BigDecimal lastPrice, List<Transaction> transactions) {
		setSecId(secId);
		setSymbol(symbol);
		setPrincipal(principal);
		setShares(shares);
		setRealizedGain(realizedGain);
		setLastPrice(lastPrice);
		setTransactions(transactions);
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
		BigDecimal p = principal;
		if (principal.abs().doubleValue() < QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
			p = BigDecimal.ZERO;
		}
		
		this.principal = p;
	}

	public BigDecimal getShares() {
		return shares;
	}

	public void setShares(BigDecimal shares) {
		BigDecimal s = shares;
		if (shares.abs().doubleValue() < QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
			s = BigDecimal.ZERO;
		}
		
		this.shares = s;
	}

	public BigDecimal getRealizedGain() {
		return realizedGain;
	}

	public void setRealizedGain(BigDecimal realizedGain) {
		this.realizedGain = realizedGain;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal price) {
		this.lastPrice = price;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
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
