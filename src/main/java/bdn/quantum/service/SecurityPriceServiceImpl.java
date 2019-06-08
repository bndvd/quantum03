package bdn.quantum.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.QuantumProperties;
import bdn.quantum.model.qplot.QChart;
import pl.zankowski.iextrading4j.api.stocks.Chart;
import pl.zankowski.iextrading4j.api.stocks.ChartRange;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.ChartRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.PriceRequestBuilder;

@Service("securityPriceService")
public class SecurityPriceServiceImpl implements SecurityPriceService {
	
	private IEXCloudClient iexTradingClient = null;
	private Map<String, StockQuoteMemento> lastStockPriceCache = new HashMap<String, StockQuoteMemento>();
	
	@Autowired
	private FundResolverService fundResolverService;
	@Autowired
	private KeyvalService keyvalService;
	
	
	private IEXCloudClient getIEXCloudClient() {
		if (iexTradingClient == null && keyvalService != null) {
			StringBuffer key = new StringBuffer();
			key.append(QuantumProperties.PROP_PREFIX).append(QuantumProperties.IEX_TOKEN);
			String iexToken = keyvalService.getKeyvalStr(key.toString());
			
			if (iexToken != null && ! iexToken.trim().equals("")) {
//				iexTradingClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_V1,
				iexTradingClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_V1_SANDBOX,
			            new IEXCloudTokenBuilder()
			            .withPublishableToken(iexToken)
			            .build());
			}
		}
		return iexTradingClient;
	}
	
	@Override
	public void configChanged() {
		iexTradingClient = null;
	}

	@Override
	public BigDecimal getLastStockPrice(String symbol) {
		BigDecimal result = getQuoteFromCache(symbol);
		
		if (result == null) {
			String querySymbol = symbol;
			String proxySymbol = fundResolverService.getStockProxy(symbol);
			if (proxySymbol != null) {
				querySymbol = proxySymbol;
			}
			
			try {
				IEXCloudClient iexClient = getIEXCloudClient();
				if (iexClient == null) {
					throw new Exception("IEX Cloud Client not initialized. Make sure IEX publishable token is configured.");
				}
				
				result = iexClient.executeRequest(new PriceRequestBuilder()
				        .withSymbol(querySymbol)
				        .build());
			}
			catch (Exception exc) {
				System.err.println("SecurityPriceServiceImpl.getLastStockPrice:: "+exc);
				result = BigDecimal.ZERO;
			}
			
			if (proxySymbol != null) {
				result = fundResolverService.convertProxyToFundValue(symbol, result);
				if (result == null) {
					result = BigDecimal.ZERO;
				}
			}
			
			if (! result.equals(BigDecimal.ZERO)) {
				lastStockPriceCache.put(symbol, new StockQuoteMemento(result));
			}
		}
		
		return result;
	}
	
	@Override
	public Iterable<QChart> getMaxChartChain(String symbol) {
		List<QChart> qChartList = null;
		
		String querySymbol = symbol;
		String proxySymbol = fundResolverService.getStockProxy(symbol);
		if (proxySymbol != null) {
			querySymbol = proxySymbol;
		}
		
		try {
			IEXCloudClient iexClient = getIEXCloudClient();
			if (iexClient == null) {
				throw new Exception("IEX Cloud Client not initialized. Make sure IEX publishable token is configured.");
			}
			
			List<Chart> chartList = iexClient.executeRequest(new ChartRequestBuilder()
					.withChartRange(ChartRange.FIVE_YEARS)
					.withSymbol(querySymbol)
					.build());
			
			if (chartList != null) {
				qChartList = new ArrayList<>();
				for (Chart c : chartList) {
					QChart qc = new QChart(symbol, c, fundResolverService);
					qChartList.add(qc);
				}
			}
		}
		catch (Exception exc) {
			System.err.println("SecurityPriceServiceImpl.getMaxDateChain:: "+exc);
			qChartList = null;
		}
		
		return qChartList;
	}
	
	public void clear() {
		lastStockPriceCache.clear();
	}

	private BigDecimal getQuoteFromCache(String symbol) {
		BigDecimal result = null;
		StockQuoteMemento memento = lastStockPriceCache.get(symbol);
		if (memento != null) {
			if (memento.getAgeInMillis() < QuantumConstants.QUOTE_CACHE_LIFE_MILLIS) {
				result = memento.getQuote();
			}
			else {
				lastStockPriceCache.remove(symbol);
			}
		}
		return result;
	}

}

class StockQuoteMemento {
	private Date timestamp = new Date();
	private BigDecimal quote;
	
	public StockQuoteMemento(BigDecimal quote) {
		this.quote = quote;
	}
	
	public BigDecimal getQuote() {
		return quote;
	}
	
	public long getAgeInMillis() {
		Date currTimestamp = new Date();
		long result = currTimestamp.getTime() - timestamp.getTime();
		return result;
	}
}