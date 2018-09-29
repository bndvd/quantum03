package bdn.quantum.service;

import java.math.BigDecimal;

public interface StockPriceService {

	public BigDecimal getLastStockPrice(String symbol);
	
}
