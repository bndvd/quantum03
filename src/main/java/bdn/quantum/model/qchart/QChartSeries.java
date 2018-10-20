package bdn.quantum.model.qchart;

import java.util.ArrayList;
import java.util.List;

public class QChartSeries {

	public static final Integer QCHART_SERIES_UNDEFINED = Integer.valueOf(0);
	public static final Integer QCHART_SERIES_PRINCIPAL = Integer.valueOf(1);
	public static final Integer QCHART_SERIES_TOTAL_US_MARKET = Integer.valueOf(2);
	public static final Integer QCHART_SERIES_USER_PORTFOLIO = Integer.valueOf(3);
	
	private Integer type = QCHART_SERIES_UNDEFINED;
	private List<QChartPoint> points = new ArrayList<>();
	
	
	public QChartSeries() {
	}
	
	public QChartSeries(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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
