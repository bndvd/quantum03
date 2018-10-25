package bdn.quantum.service;

import bdn.quantum.model.qchart.QPlot;

public interface QPlotService {

	public void clear();
	public QPlot getPlot(String plotName);
	
}
