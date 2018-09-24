package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	StockPriceService stockPriceService;

	@Autowired
	TranEntryComparator transactionComparator;
	@Autowired
	PositionComparator positionComparator;

	@Override
	public Iterable<BasketEntity> getBaskets() {
		return basketRepository.findAll();
	}
	
	@Override
	public BasketEntity getBasket(Integer id) {
		Optional<BasketEntity> b = basketRepository.findById(id);
		
		BasketEntity result = b.get();
		return result;
	}

	@Override
	public BasketEntity createBasket(BasketEntity basket) {
		return basketRepository.save(basket);
	}

	@Override
	public SecurityEntity getSecurity(Integer id) {
		Optional<SecurityEntity> s = securityRepository.findById(id);
		
		SecurityEntity result = s.get();
		return result;
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
	public Asset getAsset(Integer basketId) {
		Asset result = null;
		
		BasketEntity b = getBasket(basketId);
		if (b != null) {
			String basketName = b.getName();
			BigDecimal principal = BigDecimal.ZERO;
			BigDecimal lastValue = BigDecimal.ZERO;
			BigDecimal realizedProfit = BigDecimal.ZERO;
	
			Iterable<Position> positionIter = getPositions(basketId);
			List<Position> positions = new ArrayList<>();
			positionIter.forEach(positions::add);
			positions.sort(positionComparator);
	
			for (Position p : positions) {
				principal = principal.add(p.getPrincipal());
				BigDecimal positionValue = p.getLastPrice().multiply(p.getShares());
				if (positionValue.doubleValue() >= QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
					lastValue = lastValue.add(positionValue);
				}
				realizedProfit = realizedProfit.add(p.getRealizedProfit());
			}
	
			result = new Asset(basketId, basketName, principal, lastValue, realizedProfit);
		}
		
		return result;
	}

	@Override
	public Iterable<Asset> getAssets() {
		List<Asset> result = new ArrayList<>();

		Iterable<BasketEntity> baskets = basketRepository.findAll();
		for (BasketEntity b : baskets) {
			Integer basketId = b.getId();
			Asset a = getAsset(basketId);
			if (a != null) {
				result.add(a);
			}
		}

		return result;
	}

	@Override
	public Position getPosition(Integer secId) {
		SecurityEntity s = getSecurity(secId);
		Position result = Position.EMPTY_POSITION;
		
		if (s != null) {
			String symbol = s.getSymbol();
			BigDecimal principal = new BigDecimal(0);
			BigDecimal shares = BigDecimal.ZERO;
			BigDecimal realizedProfit = BigDecimal.ZERO;

			Iterable<TranEntity> tranIter = transactionRepository.findBySecId(secId);
			List<TranEntity> transactions = new ArrayList<>();
			tranIter.forEach(transactions::add);
			transactions.sort(transactionComparator);
			for (TranEntity t : transactions) {
				if (t.getType().equals(QuantumConstants.TRAN_TYPE_BUY)) {
					BigDecimal tPrice = t.getPrice();
					BigDecimal tShares = t.getShares();
					principal = principal.add(tPrice.multiply(tShares));
					shares = shares.add(tShares);
				}
				// using Average Cost Basis for computing cost/profit and deducting from
				// principal
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SELL)) {
					BigDecimal averageCostPerShare = principal.divide(shares, QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					BigDecimal costOfSharesSold = t.getShares().multiply(averageCostPerShare);
					BigDecimal transactionProfit = (t.getPrice().multiply(t.getShares())).subtract(costOfSharesSold);
					realizedProfit = realizedProfit.add(transactionProfit);

					principal = principal.subtract(costOfSharesSold);
					shares = shares.subtract(t.getShares());
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_DIVIDEND)) {
					realizedProfit = realizedProfit.add(t.getPrice().multiply(t.getShares()));
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SPLIT)) {
					shares = shares.multiply(t.getShares());
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_CONVERSION)) {
					shares = t.getShares();
				}
			}

			BigDecimal lastStockPrice = BigDecimal.ZERO;
			try {
				lastStockPrice = stockPriceService.getLastStockPrice(symbol);
			}
			catch (Exception e) {
				System.err.println("Exception in IEXTrading packet: " + e.getMessage());
			}

			result = new Position(secId, symbol, principal, shares, realizedProfit, lastStockPrice, transactions);
		}

		return result;
	}

	@Override
	public Iterable<Position> getPositions(Integer basketId) {
		List<Position> result = new ArrayList<>();

		Iterable<SecurityEntity> securities = securityRepository.findByBasketId(basketId);
		for (SecurityEntity s : securities) {
			Integer secId = s.getId();
			Position p = getPosition(secId);
			if (p != Position.EMPTY_POSITION) {
				result.add(p);
			}
		}

		result.sort(positionComparator);
		return result;
	}

}
