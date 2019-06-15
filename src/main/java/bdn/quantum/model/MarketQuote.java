package bdn.quantum.model;

import java.math.BigDecimal;

public class MarketQuote {

	private String symbol;
	private String mktDate;
	// adjusted close price
	private BigDecimal close;

	
	public MarketQuote(String symbol, String mktDate, BigDecimal close) {
		this.symbol = symbol;
		this.mktDate = mktDate;
		this.close = close;
	}


	public MarketQuote(MarketQuoteEntity mqe) {
		this.symbol = mqe.getSymbol();
		this.mktDate = mqe.getMktDate();
		this.close = mqe.getClose();
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public String getMktDate() {
		return mktDate;
	}


	public void setMktDate(String mktDate) {
		this.mktDate = mktDate;
	}


	public BigDecimal getClose() {
		return close;
	}


	public void setClose(BigDecimal close) {
		this.close = close;
	}

	
}
