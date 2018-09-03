package bdn.quantum.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bdn.quantum.model.PortfolioData;
import bdn.quantum.service.PortfolioService;

@RestController("portfolioController")
@RequestMapping("api/v1/")
public class PortfolioController {
	
	@Autowired
	private PortfolioService portfolioService;
	
	@RequestMapping(value = "/portfolioData", method = RequestMethod.GET)
	public PortfolioData getPortfolio() {
		return portfolioService.getPortfolioData();
	}
	
	@RequestMapping(value = "/portfolioData", method = RequestMethod.POST)
	public PortfolioData insertPortfolioData(@RequestBody PortfolioData portfolioData) {
		return portfolioService.insertPortfolioData(portfolioData);
	}
	

}
