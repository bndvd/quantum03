package bdn.quantum.service;

import bdn.quantum.model.qchart.QChartSeries;

public interface QChartService {

	Iterable<QChartSeries> getChart(String chartName);
	
}
