package bdn.quantum.service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import bdn.quantum.model.MarketQuote;
import bdn.quantum.model.MarketQuoteEntity;
import bdn.quantum.model.MarketStatus;
import bdn.quantum.model.MarketStatusEntity;
import bdn.quantum.model.iex.IEXChart;
import bdn.quantum.model.iex.IEXChartFull;
import bdn.quantum.model.iex.IEXTradeDay;
import bdn.quantum.model.qplot.QChart;
import bdn.quantum.model.util.MarketQuoteComparator;
import bdn.quantum.model.util.ModelUtils;
import bdn.quantum.repository.MarketQuoteRepository;
import bdn.quantum.repository.MarketStatusRepository;
import bdn.quantum.service.iex.IEXCloudService;

@Service("marketDataService")
public class MarketDataServiceImpl implements MarketDataService {
	
	private Map<String, Boolean> tradeDayMapCache = null; 
	
	@Autowired
	private FundResolverService fundResolverService;
	@Autowired
	private IEXCloudService iexCloudService;
	@Autowired
	private MarketQuoteRepository marketQuoteRepository;
	@Autowired
	private MarketQuoteComparator marketQuoteComparator;
	@Autowired
	private MarketStatusRepository marketStatusRepository;
	
	
	@Override
	public void configChanged() {
		iexCloudService.reset();
	}

	@Override
	public BigDecimal getLastPrice(String symbol) {
		BigDecimal result = null;
		
		String querySymbol = symbol;
		String proxySymbol = fundResolverService.getStockProxy(symbol);
		if (proxySymbol != null) {
			querySymbol = proxySymbol;
		}

		try {
			result = iexCloudService.getPrice(querySymbol);
		}
		catch (Exception exc) {
			System.err.println("MarketDataServiceImpl.getLastPrice:: " + exc);
			result = BigDecimal.ZERO;
		}

		if (proxySymbol != null) {
			result = fundResolverService.convertProxyToFundValue(symbol, result);
			if (result == null) {
				result = BigDecimal.ZERO;
			}
		}
						
		return result;
	}
	
	@Override
	public Iterable<QChart> getChartChain(String symbol) {
		return getChartChain(symbol, null);
	}
	
	@Override
	public Iterable<QChart> getChartChain(String symbol, LocalDate startDate) {
		List<QChart> qChartList = null;
		
		String querySymbol = symbol;
		String proxySymbol = fundResolverService.getStockProxy(symbol);
		if (proxySymbol != null) {
			querySymbol = proxySymbol;
		}
		
		try {
			List<MarketQuote> quoteList = loadQuoteChain(querySymbol);
			
			if (quoteList != null) {
				String startDateStr = null;
				if (startDate != null) {
					startDateStr = ModelUtils.localDateToString(startDate);
				}
				
				qChartList = new ArrayList<>();
				for (MarketQuote c : quoteList) {
					// if a start date is specified and this is chart is earlier than start date, do not add to result
					if (startDateStr!= null && c.getMktDate().compareTo(startDateStr) < 0) {
						continue;
					}
					
					QChart qc = new QChart(symbol, c, fundResolverService);
					qChartList.add(qc);
				}
			}
		}
		catch (Exception exc) {
			System.err.println("MarketDataServiceImpl.getDateChain:: "+exc);
			exc.printStackTrace();
			qChartList = null;
		}
		
		return qChartList;
	}
	
	// Read from local database the stored history. If non-existent, populate it
	// If missing recent data, populate
	private List<MarketQuote> loadQuoteChain(String symbol) {
		List<MarketQuote> result = null;
		
		loadTradeDayCache();
		
		// read quote history from database
		Iterable<MarketQuoteEntity> mqeListInRepository = marketQuoteRepository.findBySymbolOrderByMktDateAsc(symbol);
		
		// if no history in database, populate it
		if (mqeListInRepository == null || ! mqeListInRepository.iterator().hasNext()) {
			loadInitialQuoteChainIntoRepository(symbol);
			// re-query
			mqeListInRepository = marketQuoteRepository.findBySymbolOrderByMktDateAsc(symbol);
		}
		
		if (tradeDayMapCache != null && mqeListInRepository != null) {
			String firstMqeDate = null;
			Set<String> tradeDateSet = tradeDayMapCache.keySet();
			for (MarketQuoteEntity mqe : mqeListInRepository) {
				String dateInRepository = mqe.getMktDate();
				tradeDateSet.remove(dateInRepository);
				// if first quote in history, remember it
				if (firstMqeDate == null) {
					firstMqeDate = dateInRepository;
				}
			}
			
			// go through dates not already in repository
			boolean newDataAdded = false;
			for (String nextDate : tradeDateSet) {
				// ignore all trade days before the first quote date of this security; we're only looking for recent missing data
				if (nextDate.compareTo(firstMqeDate) < 0) {
					continue;
				}
				
				Boolean isTradingDay = tradeDayMapCache.get(nextDate);
				if (isTradingDay) {
					IEXChartFull cf = iexCloudService.getChart(symbol, nextDate);
					// cf can be null if the data is not (yet) available for this date (e.g., today)
					if (cf != null) {
						MarketQuoteEntity mqe = new MarketQuoteEntity(null, symbol, cf.getDate(), cf.getuClose(), cf.getuOpen(),
								cf.getuHigh(), cf.getuLow(), cf.getuVolume(), cf.getClose(), cf.getOpen(), cf.getHigh(),
								cf.getLow(), cf.getVolume());
						marketQuoteRepository.save(mqe);
						newDataAdded = true;
					}
				}
			}
			if (newDataAdded) {
				// re-query
				mqeListInRepository = marketQuoteRepository.findBySymbolOrderByMktDateAsc(symbol);
			}
		}
		// sort by date
		result = new ArrayList<>();
		for (MarketQuoteEntity mqe : mqeListInRepository) {
			result.add(new MarketQuote(mqe));
		}
		result.sort(marketQuoteComparator);
		
		return result;
	}
	
	private synchronized void loadTradeDayCache() {
		// load initial history
		if (tradeDayMapCache == null) {
			// try reading trade day history from repository
			Iterable<MarketStatusEntity> mseListInRepository = marketStatusRepository.findAllByOrderByMktDateAsc();
			
			// if repository is empty, load from data file (initial load only)
			if (mseListInRepository == null || ! mseListInRepository.iterator().hasNext()) {
				try {
					Resource resource = new ClassPathResource("data/mkt-hist-trading-dates.json");
					File file = resource.getFile();
					
					ObjectMapper objMapper = new ObjectMapper();
					List<MarketStatus> msList = objMapper.readValue(file, new TypeReference<List<MarketStatus>>(){});
					
					List<MarketStatusEntity> mseList = new ArrayList<>();
					for (MarketStatus ms : msList) {
						mseList.add(new MarketStatusEntity(ms));
					}
					marketStatusRepository.saveAll(mseList);
					
					mseListInRepository = marketStatusRepository.findAllByOrderByMktDateAsc();
				}
				catch (Exception exc) {
					System.err.println(exc.getMessage());
					return;
				}
			}
			// if still empty, error out
			if (mseListInRepository == null || ! mseListInRepository.iterator().hasNext()) {
				System.err.println("MarketDataServiceImpl.loadTradeDayCache - ERROR: could not laod initial trade day data. Exiting...");
				return;
			}
			
			// load initial trade day data into cache
			tradeDayMapCache = new HashMap<>();
			for (MarketStatusEntity mse : mseListInRepository) {
				tradeDayMapCache.put(mse.getMktDate(), mse.getOpenStatus());
			}
		}
		
		// update with the latest trade day info
		String mostRecentDateInCache = ModelUtils.getMostRecentDateStr(tradeDayMapCache.keySet());
		List<String> newDatesList = ModelUtils.getDateStringsFromStartDateStr(mostRecentDateInCache);
		
		if (newDatesList.size() > 0) {
			Map<String, MarketStatusEntity> dateStrToMSE = new HashMap<>();
			for (String d : newDatesList) {
				MarketStatusEntity mse = new MarketStatusEntity(d, false);
				dateStrToMSE.put(d, mse);
			}
			
			// set open status to true for all days returned by IEX service
			Iterable<IEXTradeDay> tdIter = iexCloudService.getTradeDays(newDatesList.size());
			for (IEXTradeDay td : tdIter) {
				String dateOpen = td.getDate();
				MarketStatusEntity mse = dateStrToMSE.get(dateOpen);
				if (mse != null) {
					mse.setOpenStatus(true);
				}
			}
			
			Iterable<MarketStatusEntity> mseIter = dateStrToMSE.values();
			marketStatusRepository.saveAll(mseIter);
			mseIter = dateStrToMSE.values();
			for (MarketStatusEntity mse : mseIter) {
				tradeDayMapCache.put(mse.getMktDate(), mse.getOpenStatus());
			}
		}
	}

	private void loadInitialQuoteChainIntoRepository(String symbol) {
		Iterable<IEXChart> iexChartIter = iexCloudService.getMaxChart(symbol);
		
		if (iexChartIter != null) {
			List<MarketQuoteEntity> mqeList = new ArrayList<>();
			for (IEXChart c : iexChartIter) {
				MarketQuoteEntity mqe = new MarketQuoteEntity(null, symbol, c.getDate(), null, null, null, null, null, c.getClose(), null, null, null, null);
				mqeList.add(mqe);
			}
			
			marketQuoteRepository.saveAll(mqeList);
		}
	}
	
}
