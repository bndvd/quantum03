package bdn.quantum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Asset;
import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.Position;
import bdn.quantum.model.SecurityEntity;
import bdn.quantum.model.TranEntity;
import bdn.quantum.model.util.PositionComparator;
import bdn.quantum.model.util.TranEntryComparator;
import bdn.quantum.repository.BasketRepository;
import bdn.quantum.repository.SecurityRepository;
import bdn.quantum.repository.TransactionRepository;

@Service("assetService")
public class AssetServiceImpl implements AssetService {

	@Autowired
	BasketRepository basketRepository;
	@Autowired
	SecurityRepository securityRepository;
	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	TranEntryComparator transactionComparator;
	@Autowired
	PositionComparator positionComparator;

	@Override
	public Iterable<BasketEntity> getBaskets() {
		return basketRepository.findAll();
	}

	@Override
	public BasketEntity createBasket(BasketEntity basket) {
		return basketRepository.save(basket);
	}

	@Override
	public Iterable<SecurityEntity> getSecurities() {
		return securityRepository.findAll();
	}

	@Override
	public Iterable<SecurityEntity> getSecuritiesInBasket(Integer basketId) {
		return securityRepository.findByBasketId(basketId);
	}

	@Override
	public SecurityEntity createSecurity(SecurityEntity security) {
		return securityRepository.save(security);
	}

	@Override
	public Iterable<Asset> getAssets() {
		List<Asset> result = new ArrayList<>();

		Iterable<BasketEntity> baskets = basketRepository.findAll();
		for (BasketEntity b : baskets) {
			Integer basketId = b.getId();
			String basketName = b.getName();
			Double principal = 0.0;
			Double value = 0.0;
			Double realizedProfit = 0.0;

			Iterable<Position> positionIter = getPositions(basketId);
			List<Position> positions = new ArrayList<>();
			positionIter.forEach(positions::add);
			positions.sort(positionComparator);

			for (Position p : positions) {
				principal += p.getPrincipal();
				Double positionValue = p.getPrice() * p.getShares();
				value += (positionValue >= 0.0) ? positionValue : 0.0;
				realizedProfit += p.getRealizedProfit();
			}

			Asset a = new Asset(basketId, basketName, principal, value, realizedProfit, positions);
			result.add(a);
		}

		return result;
	}

	@Override
	public Iterable<Position> getPositions(Integer basketId) {
		List<Position> result = new ArrayList<>();

		Iterable<SecurityEntity> securities = securityRepository.findByBasketId(basketId);
		for (SecurityEntity s : securities) {
			Integer secId = s.getId();
			String symbol = s.getSymbol();
			Double principal = 0.0;
			Double shares = 0.0;
			Double realizedProfit = 0.0;
			// TODO get price from public service
			Double price = -1.0;

			Iterable<TranEntity> tranIter = transactionRepository.findBySecId(secId);
			List<TranEntity> transactions = new ArrayList<>();
			tranIter.forEach(transactions::add);
			transactions.sort(transactionComparator);
			for (TranEntity t : transactions) {
				if (t.getType().equals(QuantumConstants.TRAN_TYPE_BUY)) {
					principal += (t.getPrice() * t.getShares());
					shares += t.getShares();
				}
				// using Average Cost Basis for computing cost/profit and deducting from
				// principal
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SELL)) {
					Double averageCostPerShare = principal / shares;
					Double costOfSharesSold = t.getShares() * averageCostPerShare;
					realizedProfit += (t.getPrice() * t.getShares()) - costOfSharesSold;

					principal -= costOfSharesSold;
					shares -= t.getShares();
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_DIVIDEND)) {
					realizedProfit += t.getPrice() * t.getShares();
				}
			}

			Position p = new Position(secId, symbol, principal, shares, realizedProfit, price, transactions);
			result.add(p);
		}

		result.sort(positionComparator);
		return result;
	}

}
