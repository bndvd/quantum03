package bdn.quantum.service;

import java.math.BigDecimal;

import bdn.quantum.model.qplot.QChart;

public interface SecurityPriceService {

	public void configChanged();
	public BigDecimal getLastStockPrice(String symbol);
	public Iterable<QChart> getMaxChartChain(String symbol);
	
}
