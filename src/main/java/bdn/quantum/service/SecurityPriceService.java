package bdn.quantum.service;

import java.math.BigDecimal;

public interface SecurityPriceService {

	public BigDecimal getLastStockPrice(String symbol);
	
}
