package bdn.quantum.model.qplot;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import bdn.quantum.service.FundResolverService;
import pl.zankowski.iextrading4j.api.stocks.Chart;

public class QChart {

	private static final DateTimeFormatter CHART_LOCALDATE_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateFormat CHART_DATE_DF = new SimpleDateFormat("yyyy-MM-dd");

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
	
	public LocalDate getLocalDate() {
		LocalDate result = LocalDate.parse(chart.getDate(), CHART_LOCALDATE_DTF);
		return result;
	}
	
	public Date getDate() {
		Date result = null;
		try {
			result = CHART_DATE_DF.parse(chart.getDate());
		}
		catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
		return result;
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
