package bdn.quantum.service;

import java.math.BigDecimal;

public interface StockPriceService {

	BigDecimal getLastStockPrice(String symbol);
	
}
