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
import bdn.quantum.model.Position;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.qchart.QChart;
import bdn.quantum.model.qchart.QPlot;
import bdn.quantum.model.qchart.QPlotPoint;
import bdn.quantum.model.qchart.QPlotSeries;
import bdn.quantum.model.util.TransactionComparator;

@Service("chartService")
public class QChartServiceImpl implements QChartService {

	@Autowired
	private AssetService assetService;
	@Autowired
	private SecurityPriceService securityPriceService;
	@Autowired
	private TransactionComparator transactionComparator;
	
	@Override
	public QPlot getChart(String chartName) {
		QPlot result = null;
		
		if (QuantumConstants.CHART_STD_GROWTH.equals(chartName)) {
			Iterable<QChart> benchmarkChartChain = securityPriceService.getMaxChartChain(QuantumConstants.CHART_STD_BENCHMARK_SYMBOL);
			if (benchmarkChartChain != null) {
				Iterable<LocalDate> dateChain = buildDateChain(benchmarkChartChain);
				Iterable<Position> positionIter = assetService.getPositions(true);
				result = buildStdGrowthChart(dateChain, positionIter, benchmarkChartChain);
			}
		}

		return result;
	}

	private QPlot buildStdGrowthChart(Iterable<LocalDate> dateChain, Iterable<Position> positionIter,
						Iterable<QChart> benchmarkChartChain) {
		if (dateChain == null || positionIter == null || benchmarkChartChain == null) {
			return null;
		}
		
		QPlot result = new QPlot(QPlot.QCHART_STD_GROWTH);
		
		QPlotSeries principalSeries = buildPrincipalChartSeries(dateChain, positionIter);
		if (principalSeries == null) {
			return null;
		}
		result.addSeries(principalSeries);
		
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
	
	
	private QPlotSeries buildPrincipalChartSeries(Iterable<LocalDate> dateChain, Iterable<Position> positionIter) {
		if (dateChain == null || positionIter == null) {
			return null;
		}
		
		QPlotSeries result = new QPlotSeries(QPlotSeries.QCHART_SERIES_PRINCIPAL);
		
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
			
			QPlotPoint point = new QPlotPoint(Integer.valueOf(pointId), ld, portfolioPrincipal);
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
			LocalDate ld = qc.getDate();
			dateToChartMap.put(ld, qc);
		}

		BigDecimal secShares = BigDecimal.ZERO;
		BigDecimal secPrincipal = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;

		for (LocalDate ld : dateChain) {
			pointId++;

			BigDecimal secValue = BigDecimal.ZERO;
			QChart qc = dateToChartMap.get(ld);

			if (qc != null) {
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

					BigDecimal openingSharePrice = qc.getOpen();
					BigDecimal shareDelta = principalDelta.divide(openingSharePrice,
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					secShares = secShares.add(shareDelta);
				}

				BigDecimal closeSharePrice = qc.getClose();
				secValue = secShares.multiply(closeSharePrice);
			}

			QPlotPoint point = new QPlotPoint(Integer.valueOf(pointId), ld, secValue);
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

	private Iterable<LocalDate> buildDateChain(Iterable<QChart> chartChain) {
		if (chartChain == null) {
			return null;
		}
		
		List<LocalDate> result = new ArrayList<LocalDate>();
		for (QChart qc : chartChain) {
			LocalDate date = qc.getDate();
			result.add(date);
		}
		
		return result;
	}
	
	private LocalDate convertDateToLocalDate(Date date) {
		return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
}
