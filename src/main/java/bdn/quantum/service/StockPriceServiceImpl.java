package bdn.quantum.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@Service("stockPriceService")
public class StockPriceServiceImpl implements StockPriceService {

	@Override
	public BigDecimal getLastStockPrice(String symbol) {
		final IEXTradingClient iexTradingClient = IEXTradingClient.create();
		final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
		        .withSymbol(symbol)
		        .build());
		
		BigDecimal result = quote.getLatestPrice();
		return result;
	}

}
