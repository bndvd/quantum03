package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.QuantumProperties;
import bdn.quantum.model.Asset;
import bdn.quantum.model.Position;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.qplot.QChart;
import bdn.quantum.model.qplot.QPlot;
import bdn.quantum.model.qplot.QPlotPoint;
import bdn.quantum.model.qplot.QPlotSeries;
import bdn.quantum.model.util.AssetSymbolManager;
import bdn.quantum.model.util.TransactionComparator;

@Service("chartService")
public class QPlotServiceImpl implements QPlotService {

	@Autowired
	private AssetService assetService;
	@Autowired
	private SecurityPriceService securityPriceService;
	@Autowired
	private KeyvalService keyvalService;
	@Autowired
	private TransactionComparator transactionComparator;
	@Autowired
	private AssetSymbolManager assetSymbolManager;
	@Autowired
	private PortfolioSimulator portfolioSimulator;
	
	private HashMap<String, QPlotMemento> qPlotCache = new HashMap<>();
	
	
	@Override
	public QPlot getPlot(String plotName) {
		if (plotName == null || plotName.trim().equals("")) {
			return null;
		}
		
		QPlot result = null;
		String benchmarkSymbol = QuantumConstants.PLOT_STD_BENCHMARK_SYMBOL;
		
		// try to read benchmark symbol from db config
		StringBuffer key = new StringBuffer();
		key.append(QuantumProperties.PROP_PREFIX).append(QuantumProperties.QPLOT_BENCHMARK_SYMBOL);
		String benchmarkSymbolInProp = keyvalService.getKeyvalStr(key.toString());
		if (benchmarkSymbolInProp != null && ! benchmarkSymbolInProp.trim().equals("")) {
			benchmarkSymbol = benchmarkSymbolInProp.trim().toUpperCase();
		}
		
		result = getQPlotFromCache(plotName, benchmarkSymbol);
		
		if (result == null) {
			Iterable<QChart> benchmarkChartChain = securityPriceService.getMaxChartChain(benchmarkSymbol);
			Iterable<LocalDate> dateChain = buildDateChain(benchmarkChartChain);
						
			if (benchmarkChartChain != null && dateChain != null) {
				if (QuantumConstants.PLOT_STD_GROWTH.equals(plotName)) {
					Iterable<Position> positionIter = assetService.getPositions(true);
					result = buildStdGrowthChart(dateChain, positionIter, benchmarkChartChain);
				}
				else if (QuantumConstants.PLOT_SIMULATED_TARGET.equals(plotName)) {
					result = buildSimTargetChart(dateChain, benchmarkChartChain);
				}
			}
			
			if (result != null) {
				addQPlotToCache(plotName, benchmarkSymbol, result);
			}
		}

		return result;
	}

	@Override
	public void clear() {
		qPlotCache.clear();
	}

	private QPlot buildStdGrowthChart(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
						Iterable<QChart> benchmarkChartChain) {
		if (dateChain == null || positionIter == null || benchmarkChartChain == null) {
			return null;
		}
		
		QPlot result = new QPlot(QPlot.QCHART_STD_GROWTH);
		
		QPlotSeries cashSeries = buildCashChartSeries(dateChain, positionIter);
		if (cashSeries == null) {
			return null;
		}
		result.addSeries(cashSeries);
		
		QPlotSeries benchmarkSeries = buildBenchmarkChartSeries(dateChain, positionIter, benchmarkChartChain);
		if (benchmarkSeries == null) {
			return null;
		}
		result.addSeries(benchmarkSeries);
		
		QPlotSeries userPotfolioSeries = buildUserPortfolioChartSeries(dateChain, positionIter);
		if (userPotfolioSeries == null) {
			return null;
		}
		result.addSeries(userPotfolioSeries);

		return result;
	}
	
	
	private QPlotSeries buildCashChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter) {
		if (dateChain == null || positionIter == null) {
			return null;
		}
		
		QPlotSeries result = new QPlotSeries(QPlotSeries.QCHART_SERIES_CASH);
		
		List<Transaction> allTranList = getSortedTransactionsFromPositions(positionIter);
		
		BigDecimal portfolioCash = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;
		
		for (LocalDate ld : dateChain) {
			pointId++;
			
			if (nextTranIndex < allTranList.size()) {
				BigDecimal cashDelta = BigDecimal.ZERO;
				Transaction t = allTranList.get(nextTranIndex);
				LocalDate nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				
				while (nextTranLocalDate.isBefore(ld) || nextTranLocalDate.isEqual(ld)) {
					
					// adjust cash value by value bought or sold
					if (t.getType().equals(QuantumConstants.TRAN_TYPE_BUY)) {
						BigDecimal tranValue = t.getShares().multiply(t.getPrice());
						cashDelta = cashDelta.add(tranValue);
					}
					else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SELL)) {
						BigDecimal tranValue = t.getShares().multiply(t.getPrice());
						cashDelta = cashDelta.subtract(tranValue);
					}
					
					nextTranIndex++;
					if (nextTranIndex >= allTranList.size()) {
						break;
					}
					t = allTranList.get(nextTranIndex);
					nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				}
				
				portfolioCash = portfolioCash.add(cashDelta);
			}
			
			QPlotPoint point = new QPlotPoint(Integer.valueOf(pointId), ld, portfolioCash);
			result.addPoint(point);
		}
		
		return result;
	}
	
	private QPlotSeries buildBenchmarkChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
						Iterable<QChart> benchmarkChartChain) {
		if (dateChain == null || benchmarkChartChain == null) {
			return null;
		}
		
		QPlotSeries result = new QPlotSeries(QPlotSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		List<QPlotPoint> points = buildPortfolioSeriesPoints(dateChain, positionIter, benchmarkChartChain);
		result.setPoints(points);
		
		return result;
	}
	
	private QPlotSeries buildUserPortfolioChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter) {
		if (dateChain == null || positionIter == null) {
			return null;
		}

		QPlotSeries result = new QPlotSeries(QPlotSeries.QCHART_SERIES_USER_PORTFOLIO);

		List<QPlotPoint> points = buildPortfolioSeriesPoints(dateChain, positionIter, null);
		result.setPoints(points);

		return result;
	}
	
	private List<QPlotPoint> buildPortfolioSeriesPoints(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
			Iterable<QChart> singlePortfolioSecChartChain) {
		if (dateChain == null || positionIter == null) {
			return null;
		}
		
		List<QPlotPoint> result = new ArrayList<>();
		
		List<List<QPlotPoint>> chartPointListsBySec = new ArrayList<>();
		for (Position p : positionIter) {
			List<Transaction> secTranList = p.getTransactions();
			
			if (secTranList != null && secTranList.size() > 0) {
				// if all positions collapse to a simulated portfolio represented by a single security (e.g., benchmark)
				// get the chart chain for that security once
				Iterable<QChart> secChartChain = singlePortfolioSecChartChain;
				if (secChartChain == null) {
					secChartChain = securityPriceService.getMaxChartChain(p.getSymbol());
				}
				
				if (secChartChain != null) {
					List<QPlotPoint> secPoints = buildSecuritySeriesPoints(dateChain, secTranList, secChartChain);
					chartPointListsBySec.add(secPoints);
				}
				else {
					System.err.println("QCharServiceImpl.buildPortfolioSeriesPoints - Could not get chart chain for symbol: " + p.getSymbol() +
							". Graph will not include this security and may be inaccurate.");
				}
			}
		}

		if (chartPointListsBySec.size() < 1) {
			return null;
		}

		// to create user portfolio series, add the security values across all
		// securities
		for (int i = 0; i < chartPointListsBySec.get(0).size(); i++) {
			Integer id = chartPointListsBySec.get(0).get(i).getId();
			LocalDate localDate = chartPointListsBySec.get(0).get(i).getDate();

			BigDecimal portfolioValue = BigDecimal.ZERO;
			for (int j = 0; j < chartPointListsBySec.size(); j++) {
				portfolioValue = portfolioValue.add(chartPointListsBySec.get(j).get(i).getValue());
			}

			QPlotPoint p = new QPlotPoint(id, localDate, portfolioValue);
			result.add(p);
		}

		return result;
	}

	private List<QPlotPoint> buildSecuritySeriesPoints(Iterable<LocalDate> dateChain, List<Transaction> secTranList,
			Iterable<QChart> secChartChain) {
		if (dateChain == null || secTranList == null || secChartChain == null) {
			return null;
		}

		List<QPlotPoint> result = new ArrayList<>();

		HashMap<LocalDate, QChart> dateToChartMap = new HashMap<>();
		for (QChart qc : secChartChain) {
			LocalDate ld = qc.getLocalDate();
			dateToChartMap.put(ld, qc);
		}

		BigDecimal secShares = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;

		for (LocalDate ld : dateChain) {
			pointId++;

			BigDecimal secValue = BigDecimal.ZERO;
			QChart qc = dateToChartMap.get(ld);

			if (qc != null) {
				if (nextTranIndex < secTranList.size()) {
					BigDecimal valueDelta = BigDecimal.ZERO;
					Transaction t = secTranList.get(nextTranIndex);
					LocalDate nextTranLocalDate = convertDateToLocalDate(t.getTranDate());

					while (nextTranLocalDate.isBefore(ld) || nextTranLocalDate.isEqual(ld)) {
						
						if (t.getType().equals(QuantumConstants.TRAN_TYPE_BUY)) {
							BigDecimal tranValue = t.getShares().multiply(t.getPrice());
							valueDelta = valueDelta.add(tranValue);
						}
						else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SELL)) {
							BigDecimal tranValue = t.getShares().multiply(t.getPrice());
							valueDelta = valueDelta.subtract(tranValue);
						}

						nextTranIndex++;
						if (nextTranIndex >= secTranList.size()) {
							break;
						}
						t = secTranList.get(nextTranIndex);
						nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
					}

					BigDecimal shareDelta = valueDelta.divide(qc.getClose(),
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					secShares = secShares.add(shareDelta);
				}

				secValue = secShares.multiply(qc.getClose());
			}

			QPlotPoint point = new QPlotPoint(Integer.valueOf(pointId), ld, secValue);
			result.add(point);
		}

		return result;
	}
	
	
	// Simulated Target Portfolio Chart
	private QPlot buildSimTargetChart(Iterable<LocalDate> dateChain, Iterable<QChart> benchmarkChartChain) {
		// read in target ratios
		Iterable<Asset> assets = assetService.getAssets();
		HashMap<String, BigDecimal> symbolToTargetRatioMap = new HashMap<>();
		for (Asset a : assets) {
			BigDecimal targetRatio = a.getTargetRatio();
			// only consider non-zero target ratios
			if (targetRatio != null && targetRatio.abs().doubleValue() >= QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
				String stockSymbol = assetSymbolManager.getSymbolForAsset(a.getBasketId());
				symbolToTargetRatioMap.put(stockSymbol, targetRatio);
			}
		}
		
		portfolioSimulator
		
	}
	

	private List<Transaction> getSortedTransactionsFromPositions(Iterable<Position> positionIter) {
		if (positionIter == null) {
			return null;
		}
		
		List<Transaction> result = new ArrayList<>();
		for (Position p : positionIter) {
			List<Transaction> tranList = p.getTransactions();
			if (tranList != null) {
				result.addAll(tranList);
			}
		}
		result.sort(transactionComparator);
		
		return result;
	}

	private Iterable<LocalDate> buildDateChain(Iterable<QChart> chartChain) {
		if (chartChain == null) {
			return null;
		}
		
		List<LocalDate> result = new ArrayList<LocalDate>();
		for (QChart qc : chartChain) {
			LocalDate date = qc.getLocalDate();
			result.add(date);
		}
		
		return result;
	}
	
	private LocalDate convertDateToLocalDate(Date date) {
		return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
	private QPlot getQPlotFromCache(String plotName, String benchmarkSymbol) {
		if (plotName == null || plotName.trim().equals("") || 
						benchmarkSymbol == null || benchmarkSymbol.trim().equals("")) {
			return null;
		}
		
		QPlot result = null;
		
		String cacheKey = getCacheKey(plotName, benchmarkSymbol);
		QPlotMemento memento = qPlotCache.get(cacheKey);
		if (memento != null) {
			if (memento.getAgeInMillis() < QuantumConstants.PLOT_CACHE_LIFE_MILLIS) {
				result = memento.getQPlot();
			}
			else {
				qPlotCache.remove(cacheKey);
			}
		}
		return result;
	}
	
	private void addQPlotToCache(String plotName, String benchmarkSymbol, QPlot qPlot) {
		if (plotName == null || plotName.trim().equals("") || benchmarkSymbol == null
				|| benchmarkSymbol.trim().equals("") || qPlot == null) {
			return;
		}
		String cacheKey = getCacheKey(plotName, benchmarkSymbol);
		qPlotCache.put(cacheKey, new QPlotMemento(qPlot));
	}
	
	private String getCacheKey(String plotName, String benchmarkSymbol) {
		if (plotName == null || plotName.trim().equals("") || benchmarkSymbol == null
				|| benchmarkSymbol.trim().equals("")) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append(plotName).append("#!#").append(benchmarkSymbol);
		return result.toString();
	}

}

class QPlotMemento {
	private Date timestamp = new Date();
	private QPlot qPlot;
	
	public QPlotMemento(QPlot qPlot) {
		this.qPlot = qPlot;
	}
	
	public QPlot getQPlot() {
		return qPlot;
	}
	
	public long getAgeInMillis() {
		Date currTimestamp = new Date();
		long result = currTimestamp.getTime() - timestamp.getTime();
		return result;
	}
}
