package bdn.quantum.service;

import java.math.BigDecimal;

import pl.zankowski.iextrading4j.api.stocks.Chart;

public interface SecurityPriceService {

	public BigDecimal getLastStockPrice(String symbol);
	public Iterable<Chart> getMaxChartChain(String symbol);
	
}
