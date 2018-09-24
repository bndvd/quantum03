package bdn.quantum.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@Service("stockPriceService")
public class StockPriceServiceImpl implements StockPriceService {
	
	private final IEXTradingClient iexTradingClient = IEXTradingClient.create();
	private Map<String, StockQuoteMemento> lastStockPriceCache = new HashMap<String, StockQuoteMemento>();
	
	@Override
	public BigDecimal getLastStockPrice(String symbol) {
		BigDecimal result = getQuoteFromCache(symbol);
		
		if (result == null) {
			final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
			        .withSymbol(symbol)
			        .build());
			
			result = quote.getLatestPrice();
			lastStockPriceCache.put(symbol, new StockQuoteMemento(result));
		}
		
		return result;
	}
	
	private BigDecimal getQuoteFromCache(String symbol) {
		BigDecimal result = null;
		StockQuoteMemento memento = lastStockPriceCache.get(symbol);
		if (memento != null && memento.getAgeInMillis() < QuantumConstants.QUOTE_CACHE_LIFE_MILLIS) {
			result = memento.getQuote();
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