package bdn.quantum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Position;
import bdn.quantum.model.chart.ChartSeries;

@Service("chartService")
public class ChartServiceImpl implements ChartService {

	@Autowired
	private AssetService assetService;
	@Autowired
	private SecurityPriceService securityPriceService;
	
	@Override
	public Iterable<ChartSeries> getChart(String chartName) {
		Iterable<ChartSeries> result = null;
		
		if (QuantumConstants.CHART_STD_GROWTH.equals(chartName)) {
			Iterable<Position> positions = assetService.getPositions(true);
			ChartSeries principalSeries = buildHistory(positions);
			result = buildStdGrowthChart(principalSeries, positions);
		}

		return result;
	}

	private ChartSeries buildHistory(Iterable<Position> positions) {
		// TODO
		return null;
	}

	private Iterable<ChartSeries> buildStdGrowthChart(ChartSeries principalSeries, Iterable<Position> positions) {
		List<ChartSeries> result = new ArrayList<>();
		
		// TODO

		return result;
	}

}
