package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Position;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.qchart.QChart;
import bdn.quantum.model.qchart.QChartPoint;
import bdn.quantum.model.qchart.QChartSeries;
import bdn.quantum.model.util.TransactionComparator;
import pl.zankowski.iextrading4j.api.stocks.Chart;

@Service("chartService")
public class QChartServiceImpl implements QChartService {

	private static final DateTimeFormatter CHART_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

	@Autowired
	private AssetService assetService;
	@Autowired
	private SecurityPriceService securityPriceService;
	@Autowired
	private TransactionComparator transactionComparator;
	
	@Override
	public QChart getChart(String chartName) {
		QChart result = null;
		
		if (QuantumConstants.CHART_STD_GROWTH.equals(chartName)) {
			Iterable<Chart> benchmarkChartChain = securityPriceService.getMaxChartChain(QuantumConstants.CHART_STD_BENCHMARK_SYMBOL);
			if (benchmarkChartChain != null) {
				Iterable<LocalDate> dateChain = buildDateChain(benchmarkChartChain);
				Iterable<Position> positionIter = assetService.getPositions(true);
				result = buildStdGrowthChart(dateChain, positionIter, benchmarkChartChain);
			}
		}

		return result;
	}

	private QChart buildStdGrowthChart(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
						Iterable<Chart> benchmarkChartChain) {
		if (dateChain == null || positionIter == null || benchmarkChartChain == null) {
			return null;
		}
		
		QChart result = new QChart(QChart.QCHART_STD_GROWTH);
		
		QChartSeries principalSeries = buildPrincipalChartSeries(dateChain, positionIter);
		if (principalSeries == null) {
			return null;
		}
		result.addSeries(principalSeries);
		
		QChartSeries benchmarkSeries = buildBenchmarkChartSeries(dateChain, positionIter, benchmarkChartChain);
		if (benchmarkSeries == null) {
			return null;
		}
		result.addSeries(benchmarkSeries);
		
		QChartSeries userPotfolioSeries = buildUserPortfolioChartSeries(dateChain, positionIter);
		if (userPotfolioSeries == null) {
			return null;
		}
		result.addSeries(userPotfolioSeries);

		return result;
	}
	
	
	private QChartSeries buildPrincipalChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter) {
		if (dateChain == null || positionIter == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_PRINCIPAL);
		
		List<Transaction> allTranList = getSortedTransactionsFromPositions(positionIter);
		
		BigDecimal portfolioPrincipal = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;
		// running principal for each security; deltas at transaction determine adjustments to portfolio principal
		HashMap<Integer, BigDecimal> secIdToPrincipalMap = new HashMap<>();
		
		for (LocalDate ld : dateChain) {
			pointId++;
			
			if (nextTranIndex < allTranList.size()) {
				BigDecimal principalDelta = BigDecimal.ZERO;
				Transaction t = allTranList.get(nextTranIndex);
				LocalDate nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				
				while (nextTranLocalDate.isBefore(ld) || nextTranLocalDate.isEqual(ld)) {
					Integer secId = t.getSecId();
					
					BigDecimal oldSecPrincipal = secIdToPrincipalMap.get(secId);
					if (oldSecPrincipal == null) {
						oldSecPrincipal = BigDecimal.ZERO;
						secIdToPrincipalMap.put(secId, oldSecPrincipal);
					}
					
					BigDecimal newSecPrincipal = t.getPrincipal();
					principalDelta = principalDelta.add(newSecPrincipal.subtract(oldSecPrincipal));
					secIdToPrincipalMap.put(secId, newSecPrincipal);
					
					nextTranIndex++;
					if (nextTranIndex >= allTranList.size()) {
						break;
					}
					t = allTranList.get(nextTranIndex);
					nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				}
				
				portfolioPrincipal = portfolioPrincipal.add(principalDelta);
			}
			
			QChartPoint point = new QChartPoint(Integer.valueOf(pointId), ld, portfolioPrincipal);
			result.addPoint(point);
		}
		
		return result;
	}
	
	private QChartSeries buildBenchmarkChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
						Iterable<Chart> benchmarkChartChain) {
		if (dateChain == null || benchmarkChartChain == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		List<QChartPoint> points = buildPortfolioSeriesPoints(dateChain, positionIter, benchmarkChartChain);
		result.setPoints(points);
		
		return result;
	}
	
	private QChartSeries buildUserPortfolioChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter) {
		if (dateChain == null || positionIter == null) {
			return null;
		}

		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_USER_PORTFOLIO);

		List<QChartPoint> points = buildPortfolioSeriesPoints(dateChain, positionIter, null);
		result.setPoints(points);

		return result;
	}
	
	private List<QChartPoint> buildPortfolioSeriesPoints(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
			Iterable<Chart> singlePortfolioSecChartChain) {
		if (dateChain == null || positionIter == null) {
			return null;
		}
		
		List<QChartPoint> result = new ArrayList<>();
		
		List<List<QChartPoint>> chartPointListsBySec = new ArrayList<>();
		for (Position p : positionIter) {
			List<Transaction> secTranList = p.getTransactions();
			
			if (secTranList != null && secTranList.size() > 0) {
				// if all positions collapse to a simulated portfolio represented by a single security (e.g., benchmark)
				// get the chart chain for that security once
				Iterable<Chart> secChartChain = singlePortfolioSecChartChain;
				if (secChartChain == null) {
					secChartChain = securityPriceService.getMaxChartChain(p.getSymbol());
				}
				
				if (secChartChain != null) {
					List<QChartPoint> secPoints = buildSecuritySeriesPoints(dateChain, secTranList, secChartChain);
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

			QChartPoint p = new QChartPoint(id, localDate, portfolioValue);
			result.add(p);
		}

		return result;
	}

	private List<QChartPoint> buildSecuritySeriesPoints(Iterable<LocalDate> dateChain, List<Transaction> secTranList,
			Iterable<Chart> secChartChain) {
		if (dateChain == null || secTranList == null || secChartChain == null) {
			return null;
		}

		List<QChartPoint> result = new ArrayList<>();

		HashMap<LocalDate, Chart> dateToChartMap = new HashMap<>();
		for (Chart c : secChartChain) {
			LocalDate ld = LocalDate.parse(c.getDate(), CHART_DTF);
			dateToChartMap.put(ld, c);
		}

		BigDecimal secShares = BigDecimal.ZERO;
		BigDecimal secPrincipal = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;

		for (LocalDate ld : dateChain) {
			pointId++;

			BigDecimal secValue = BigDecimal.ZERO;
			Chart c = dateToChartMap.get(ld);

			if (c != null) {
				if (nextTranIndex < secTranList.size()) {
					BigDecimal principalDelta = BigDecimal.ZERO;
					Transaction t = secTranList.get(nextTranIndex);
					LocalDate nextTranLocalDate = convertDateToLocalDate(t.getTranDate());

					while (nextTranLocalDate.isBefore(ld) || nextTranLocalDate.isEqual(ld)) {
						BigDecimal oldSecPrincipal = secPrincipal;
						BigDecimal newSecPrincipal = t.getPrincipal();
						principalDelta = principalDelta.add(newSecPrincipal.subtract(oldSecPrincipal));
						secPrincipal = newSecPrincipal;

						nextTranIndex++;
						if (nextTranIndex >= secTranList.size()) {
							break;
						}
						t = secTranList.get(nextTranIndex);
						nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
					}

					BigDecimal openingSharePrice = c.getOpen();
					BigDecimal shareDelta = principalDelta.divide(openingSharePrice,
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					secShares = secShares.add(shareDelta);
				}

				BigDecimal closeSharePrice = c.getClose();
				secValue = secShares.multiply(closeSharePrice);
			}

			QChartPoint point = new QChartPoint(Integer.valueOf(pointId), ld, secValue);
			result.add(point);
		}

		return result;
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

	private Iterable<LocalDate> buildDateChain(Iterable<Chart> chartChain) {
		if (chartChain == null) {
			return null;
		}
		
		List<LocalDate> result = new ArrayList<LocalDate>();
		for (Chart c : chartChain) {
			LocalDate date = LocalDate.parse(c.getDate(), CHART_DTF);
			result.add(date);
		}
		
		return result;
	}
	
	private LocalDate convertDateToLocalDate(Date date) {
		return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
}
