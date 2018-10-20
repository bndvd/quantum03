package bdn.quantum.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Position;
import bdn.quantum.model.qchart.QChartSeries;
import pl.zankowski.iextrading4j.api.stocks.Chart;

@Service("chartService")
public class QChartServiceImpl implements QChartService {

	private static final DateTimeFormatter CHART_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

	@Autowired
	private AssetService assetService;
	@Autowired
	private SecurityPriceService securityPriceService;
	
	@Override
	public Iterable<QChartSeries> getChart(String chartName) {
		Iterable<QChartSeries> result = null;
		
		if (QuantumConstants.CHART_STD_GROWTH.equals(chartName)) {
			Iterable<Chart> benchmarkChartChain = securityPriceService.getMaxChartChain(QuantumConstants.CHART_STD_BENCHMARK_SYMBOL);
			if (benchmarkChartChain != null) {
				Iterable<LocalDate> dateChain = buildDateChain(benchmarkChartChain);
				Iterable<Position> positions = assetService.getPositions(true);
				result = buildStdGrowthChart(dateChain, benchmarkChartChain, positions);
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
			System.out.println(date);
		}
		
		return result;
	}
	
	private Iterable<QChartSeries> buildStdGrowthChart(Iterable<LocalDate> dateChain, 
						Iterable<Chart> benchmarkChartChain, Iterable<Position> positions) {
		List<QChartSeries> result = new ArrayList<>();
		
		// TODO

		return result;
	}

}
