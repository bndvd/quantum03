package bdn.quantum.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.qchart.QChart;
import bdn.quantum.service.QChartService;

@RestController("chartController")
@RequestMapping(QuantumConstants.REST_URL_BASE)
public class QChartController {

	@Autowired
	private QChartService qChartService;
	
	@RequestMapping(value = "/chart/{chartName}", method = RequestMethod.GET)
	public QChart getChart(@PathVariable(value="chartName") String chartName) {
		return qChartService.getChart(chartName);
	}
	

	
}
