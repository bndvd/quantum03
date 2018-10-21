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
				Iterable<Transaction> tranIter = getSortedTransactionsFromPositions(positionIter);
				result = buildStdGrowthChart(dateChain, benchmarkChartChain, tranIter);
			}
		}

		return result;
	}

	private Iterable<Transaction> getSortedTransactionsFromPositions(Iterable<Position> positionIter) {
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
	
	private QChart buildStdGrowthChart(Iterable<LocalDate> dateChain, 
						Iterable<Chart> benchmarkChartChain, Iterable<Transaction> tranIter) {
		if (dateChain == null || benchmarkChartChain == null || tranIter == null) {
			return null;
		}
		
		QChart result = new QChart(QChart.QCHART_STD_GROWTH);
		
		List<Transaction> tranList = new ArrayList<>();
		for (Transaction t : tranIter) {
			tranList.add(t);
		}
		
		QChartSeries principalSeries = buildPrincipalChartSeries(dateChain, tranList);
		if (principalSeries == null) {
			return null;
		}
		result.addSeries(principalSeries);
		
		QChartSeries benchmarkSeries = buildBenchmarkChartSeries(principalSeries, benchmarkChartChain);
		if (benchmarkSeries == null) {
			return null;
		}
		result.addSeries(benchmarkSeries);
		
		QChartSeries userPotfolioSeries = buildUserPortfolioChartSeries(dateChain, tranList);
		if (userPotfolioSeries == null) {
			return null;
		}
//		result.addSeries(userPotfolioSeries);

		return result;
	}
	
	
	private QChartSeries buildPrincipalChartSeries(Iterable<LocalDate> dateChain, List<Transaction> tranList) {
		if (dateChain == null || tranList == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_PRINCIPAL);
		
		BigDecimal portfolioPrincipal = BigDecimal.ZERO;
		int nextTranIndex = 0;
		int pointId = 0;
		// running principal for each security; deltas at transaction determine adjustments to portfolio principal
		HashMap<Integer, BigDecimal> secIdToPrincipalMap = new HashMap<>();
		
		for (LocalDate ld : dateChain) {
			pointId++;
			
			if (nextTranIndex < tranList.size()) {
				BigDecimal principalAdjust = BigDecimal.ZERO;
				Transaction t = tranList.get(nextTranIndex);
				LocalDate nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				
				while (nextTranLocalDate.isBefore(ld) || nextTranLocalDate.isEqual(ld)) {
					Integer secId = t.getSecId();
					
					BigDecimal oldSecPrincipal = secIdToPrincipalMap.get(secId);
					if (oldSecPrincipal == null) {
						oldSecPrincipal = BigDecimal.ZERO;
						secIdToPrincipalMap.put(secId, oldSecPrincipal);
					}
					
					BigDecimal newSecPrincipal = t.getPrincipal();
					principalAdjust = principalAdjust.add(newSecPrincipal.subtract(oldSecPrincipal));
					secIdToPrincipalMap.put(secId, newSecPrincipal);
					
					nextTranIndex++;
					if (nextTranIndex >= tranList.size()) {
						break;
					}
					t = tranList.get(nextTranIndex);
					nextTranLocalDate = convertDateToLocalDate(t.getTranDate());
				}
				
				portfolioPrincipal = portfolioPrincipal.add(principalAdjust);
			}
			
			QChartPoint point = new QChartPoint(Integer.valueOf(pointId), ld, portfolioPrincipal);
			result.addPoint(point);
		}
		
		return result;
	}
	
	private QChartSeries buildBenchmarkChartSeries(QChartSeries principalSeries, Iterable<Chart> benchmarkChartChain) {
		if (principalSeries == null || benchmarkChartChain == null || principalSeries.getPoints() == null) {
			return null;
		}
		
		List<Chart> benchmarkChartList = new ArrayList<Chart>();
		for (Chart c : benchmarkChartChain) {
			benchmarkChartList.add(c);
		}
		if (principalSeries.getPoints().size() != benchmarkChartList.size()) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		BigDecimal shares = BigDecimal.ZERO;
		BigDecimal oldPrincipal = BigDecimal.ZERO;
		
		List<QChartPoint> principalPoints = principalSeries.getPoints();
		for (int i = 0; i < principalPoints.size(); i++) {
			QChartPoint p = principalPoints.get(i);
			BigDecimal newPrincipal = p.getValue();
			BigDecimal principalDelta = newPrincipal.subtract(oldPrincipal);
			// if non-zero change in principal, calculate the # additional shares we will add at the price
			// of the opening share price
			if (principalDelta.abs().doubleValue() >= QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
				BigDecimal openSharePrice = benchmarkChartList.get(i).getOpen();
				BigDecimal sharesDelta = principalDelta.divide(openSharePrice,
								QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
				shares = shares.add(sharesDelta);
			}
			
			BigDecimal closeSharePrice = benchmarkChartList.get(i).getClose();
			BigDecimal benchmarkValue = shares.multiply(closeSharePrice);
			
			QChartPoint point = new QChartPoint(p.getId(), p.getDate(), benchmarkValue);
			result.addPoint(point);
			
			oldPrincipal = newPrincipal;
		}
		
		return result;
	}

	private QChartSeries buildUserPortfolioChartSeries(Iterable<LocalDate> dateChain, List<Transaction> tranList) {
		if (dateChain == null || tranList == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_USER_PORTFOLIO);
		
		// TODO
		
		return result;
	}
	
	private LocalDate convertDateToLocalDate(Date date) {
		return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
}
