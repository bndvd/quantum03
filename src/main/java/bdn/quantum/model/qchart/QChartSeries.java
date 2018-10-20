package bdn.quantum.model.qchart;

import java.util.ArrayList;
import java.util.List;

public class QChartSeries {

	private String seriesName = "series";
	private List<QChartPoint> points = new ArrayList<>();
	
	
	public QChartSeries() {
	}
	
	public QChartSeries(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public List<QChartPoint> getPoints() {
		return points;
	}

	public void setPoints(List<QChartPoint> points) {
		this.points = points;
	}
	
	public void addPoint(QChartPoint point) {
		points.add(point);
	}
}
