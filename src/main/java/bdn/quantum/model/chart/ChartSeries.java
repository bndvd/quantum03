package bdn.quantum.model.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartSeries {

	private String seriesName = "series";
	private List<ChartPoint> points = new ArrayList<>();
	
	
	public ChartSeries() {
	}
	
	public ChartSeries(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public List<ChartPoint> getPoints() {
		return points;
	}

	public void setPoints(List<ChartPoint> points) {
		this.points = points;
	}
	
	public void addPoint(ChartPoint point) {
		points.add(point);
	}
}
