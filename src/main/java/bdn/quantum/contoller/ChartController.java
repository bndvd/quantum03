package bdn.quantum.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.chart.ChartSeries;
import bdn.quantum.service.ChartService;

@RestController("chartController")
@RequestMapping(QuantumConstants.REST_URL_BASE)
public class ChartController {

	@Autowired
	private ChartService chartService;
	
	@RequestMapping(value = "/chart/{chartName}", method = RequestMethod.GET)
	public Iterable<ChartSeries> getChart(@PathVariable(value="chartName") String chartName) {
		return chartService.getChart(chartName);
	}
	

	
}
