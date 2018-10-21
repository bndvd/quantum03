package bdn.quantum.service;

import java.math.BigDecimal;

import bdn.quantum.model.qchart.QChart;

public interface SecurityPriceService {

	public BigDecimal getLastStockPrice(String symbol);
	public Iterable<QChart> getMaxChartChain(String symbol);
	
}
