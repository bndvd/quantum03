package bdn.quantum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.PortfolioData;
import bdn.quantum.model.SecurityEntity;
import bdn.quantum.model.TranEntity;

@Service("portfolioService")
public class PortfolioServiceImpl implements PortfolioService {

	@Autowired
	AssetService assetService;
	@Autowired
	TransactionService transactionService;
	
	@Override
	public PortfolioData getPortfolioData() {
		List<BasketEntity> basketEntities = assetService.getBaskets();
		List<SecurityEntity> securityEntities = assetService.getSecurities();
		List<TranEntity> tranEntities = new ArrayList<TranEntity>();
		
		for (SecurityEntity s : securityEntities) {
			List<TranEntity> tList = transactionService.getTransactions(s.getId());
			tranEntities.addAll(tList);
		}
		
		PortfolioData result = new PortfolioData(basketEntities, securityEntities, tranEntities);
		return result;
	}

	@Override
	public PortfolioData insertPortfolioData(PortfolioData portfolioData) {
		for (BasketEntity b : portfolioData.getBasketEntities()) {
			assetService.createBasket(b);
		}
		for (SecurityEntity s : portfolioData.getSecurityEntities()) {
			assetService.createSecurity(s);
		}
		for (TranEntity t : portfolioData.getTranEntities()) {
			transactionService.createTransaction(t);
		}
		
		return portfolioData;
	}

}
