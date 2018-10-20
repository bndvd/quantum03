package bdn.quantum.model.qchart;

import java.math.BigDecimal;
import java.time.LocalDate;

public class QChartPoint {

	private int id;
	private LocalDate date;
	private BigDecimal value;
	
	
	public QChartPoint(int id, LocalDate date, BigDecimal value) {
		this.id = id;
		this.date = date;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
}
