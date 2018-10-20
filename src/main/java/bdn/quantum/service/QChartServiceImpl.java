package bdn.quantum.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.qchart.QChart;
import bdn.quantum.model.qchart.QChartSeries;
import pl.zankowski.iextrading4j.api.stocks.Chart;

@Service("chartService")
public class QChartServiceImpl implements QChartService {

	private static final DateTimeFormatter CHART_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private SecurityPriceService securityPriceService;
	
	@Override
	public QChart getChart(String chartName) {
		QChart result = null;
		
		if (QuantumConstants.CHART_STD_GROWTH.equals(chartName)) {
			Iterable<Chart> benchmarkChartChain = securityPriceService.getMaxChartChain(QuantumConstants.CHART_STD_BENCHMARK_SYMBOL);
			if (benchmarkChartChain != null) {
				Iterable<LocalDate> dateChain = buildDateChain(benchmarkChartChain);
				Iterable<Transaction> tranIter = transactionService.getTransactions();
				result = buildStdGrowthChart(dateChain, benchmarkChartChain, tranIter);
			}
		}

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
		
		QChartSeries series = buildPrincipalChartSeries(dateChain, tranList);
		if (series == null) {
			return null;
		}
		result.addSeries(series);
		
		series = buildBenchmarkChartSeries(dateChain, benchmarkChartChain);
		if (series == null) {
			return null;
		}
		result.addSeries(series);
		
		series = buildUserPortfolioChartSeries(dateChain, tranList);
		if (series == null) {
			return null;
		}
		result.addSeries(series);

		return result;
	}
	
	
	private QChartSeries buildPrincipalChartSeries(Iterable<LocalDate> dateChain, List<Transaction> tranList) {
		if (dateChain == null || tranList == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		// TODO
		
		return result;
	}
	
	private QChartSeries buildBenchmarkChartSeries(Iterable<LocalDate> dateChain, Iterable<Chart> benchmarkChartChain) {
		if (dateChain == null || benchmarkChartChain == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		// TODO
		
		return result;
	}

	private QChartSeries buildUserPortfolioChartSeries(Iterable<LocalDate> dateChain, List<Transaction> tranList) {
		if (dateChain == null || tranList == null) {
			return null;
		}
		
		QChartSeries result = new QChartSeries(QChartSeries.QCHART_SERIES_TOTAL_US_MARKET);
		
		// TODO
		
		return result;
	}
	
}
