package bdn.quantum.model.qchart;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import bdn.quantum.service.FundResolverService;
import pl.zankowski.iextrading4j.api.stocks.Chart;

public class QChart {

	private static final DateTimeFormatter CHART_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

	private String symbol;
	private Chart chart;
	private FundResolverService fundResolverService;
	
	private String proxySymbol = null;
	
	
	public QChart(String symbol, Chart chart, FundResolverService fundResolverService) {
		this.symbol = symbol;
		this.chart = chart;
		this.fundResolverService = fundResolverService;
		this.proxySymbol = fundResolverService.getStockProxy(symbol);
	}
	
	public LocalDate getDate() {
		LocalDate ld = LocalDate.parse(chart.getDate(), CHART_DTF);
		return ld;
	}
	
	public BigDecimal getOpen() {
		BigDecimal result = chart.getOpen();
		if (proxySymbol != null) {
			result = fundResolverService.convertProxyToFundValue(symbol, result);
		}
		return result;
	}
	
	public BigDecimal getClose() {
		BigDecimal result = chart.getClose();
		if (proxySymbol != null) {
			result = fundResolverService.convertProxyToFundValue(symbol, result);
		}
		return result;
	}
	
}
