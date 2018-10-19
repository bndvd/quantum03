package bdn.quantum.service;

import bdn.quantum.model.chart.ChartSeries;

public interface ChartService {

	Iterable<ChartSeries> getChart(String chartName);
	
}
