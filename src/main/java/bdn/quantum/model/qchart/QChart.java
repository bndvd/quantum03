package bdn.quantum.model.qchart;

import java.util.ArrayList;
import java.util.List;

public class QChart {

	public static final Integer QCHART_STD_UNDEFINED = Integer.valueOf(0);
	public static final Integer QCHART_STD_GROWTH = Integer.valueOf(1);
	
	private Integer type = QCHART_STD_UNDEFINED;
	private List<QChartSeries> seriesList = new ArrayList<>();
	
	
	public QChart() {
	}
	
	public QChart(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<QChartSeries> getSeriesList() {
		return seriesList;
	}

	public void setSeriesList(List<QChartSeries> seriesList) {
		this.seriesList = seriesList;
	}

	public void addSeries(QChartSeries series) {
		if (series == null) {
			return;
		}
		seriesList.add(series);
	}
}
