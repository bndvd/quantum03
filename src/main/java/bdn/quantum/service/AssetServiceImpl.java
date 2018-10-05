package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.QuantumProperties;
import bdn.quantum.model.Asset;
import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.Position;
import bdn.quantum.model.Security;
import bdn.quantum.model.SecurityEntity;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.util.AssetComparator;
import bdn.quantum.model.util.PositionComparator;
import bdn.quantum.model.util.SecurityComparator;
import bdn.quantum.model.util.TransactionComparator;
import bdn.quantum.repository.BasketRepository;
import bdn.quantum.repository.SecurityRepository;

@Service("assetService")
public class AssetServiceImpl implements AssetService {

	@Autowired
	private BasketRepository basketRepository;
	@Autowired
	private SecurityRepository securityRepository;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private KeyvalService keyvalService;
	@Autowired
	private StockPriceService stockPriceService;

	@Autowired
	private PositionComparator positionComparator;
	@Autowired
	private AssetComparator assetComparator;
	@Autowired
	private SecurityComparator securityComparator;
	@Autowired
	private TransactionComparator transactionComparator;

	@Override
	public Iterable<BasketEntity> getBaskets() {
		return basketRepository.findAll();
	}

	@Override
	public BasketEntity getBasket(Integer id) {
		Optional<BasketEntity> b = basketRepository.findById(id);

		BasketEntity result = null;
		if (b.isPresent()) {
			result = b.get();
		}
		return result;
	}

	@Override
	public BasketEntity createBasket(BasketEntity basket) {
		return basketRepository.save(basket);
	}

	@Override
	public Security getSecurity(Integer id) {
		Optional<SecurityEntity> s = securityRepository.findById(id);
		
		Security result = null;
		if (s.isPresent()) {
			SecurityEntity se = s.get();
			result = new Security(se);
		}
		return result;
	}

	@Override
	public Iterable<Security> getSecurities() {
		Iterable<SecurityEntity> seIter = securityRepository.findAll();
		return convertSecurityEntityIterableToSortedSecurityIterable(seIter);
	}

	@Override
	public Iterable<Security> getSecuritiesInBasket(Integer basketId) {
		Iterable<SecurityEntity> seIter = securityRepository.findByBasketId(basketId);
		return convertSecurityEntityIterableToSortedSecurityIterable(seIter);
	}

	@Override
	public Security createSecurity(Security security) {
		if (security == null) {
			return null;
		}
		SecurityEntity se = new SecurityEntity(security.getId(), security.getBasketId(), security.getSymbol());
		se = securityRepository.save(se);

		Security result = null;
		if (se != null) {
			result = new Security(se);
		}
		return result;
	}

	private Iterable<Security> convertSecurityEntityIterableToSortedSecurityIterable(Iterable<SecurityEntity> seIter) {
		List<Security> result = new ArrayList<>();
		for (SecurityEntity se : seIter) {
			Security s = new Security(se);
			result.add(s);
		}
		result.sort(securityComparator);
		
		return result;
	}

	@Override
	public Asset getAsset(Integer basketId) {
		Asset result = null;

		BasketEntity b = getBasket(basketId);
		if (b != null) {
			String basketName = b.getName();
			BigDecimal principal = BigDecimal.ZERO;
			BigDecimal totalPrincipal = BigDecimal.ZERO;
			BigDecimal lastValue = BigDecimal.ZERO;
			BigDecimal realizedGain = BigDecimal.ZERO;
			BigDecimal realizedGainYtd = BigDecimal.ZERO;
			BigDecimal realizedGainYtdTax = BigDecimal.ZERO;
			BigDecimal unrealizedGain = BigDecimal.ZERO;

			Iterable<Position> positionIter = getPositions(basketId);
			List<Position> positions = new ArrayList<>();
			positionIter.forEach(positions::add);
			positions.sort(positionComparator);

			for (Position p : positions) {
				principal = principal.add(p.getPrincipal());
				totalPrincipal = totalPrincipal.add(p.getTotalPrincipal());
				BigDecimal positionValue = p.getLastValue();
				if (positionValue.doubleValue() >= QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
					lastValue = lastValue.add(positionValue);
				}
				realizedGain = realizedGain.add(p.getRealizedGain());
				realizedGainYtd = realizedGainYtd.add(p.getRealizedGainYtd());
				realizedGainYtdTax = realizedGainYtdTax.add(p.getRealizedGainYtdTax());
				unrealizedGain = unrealizedGain.add(p.getUnrealizedGain());
			}

			result = new Asset(basketId, basketName, principal, totalPrincipal, lastValue, realizedGain,
					realizedGainYtd, realizedGainYtdTax, unrealizedGain);
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
		result.sort(assetComparator);
		
		computeAssetStatistics(result);

		return result;
	}
	
	// compute asset statistics in context of portfolio
	private void computeAssetStatistics(List<Asset> assets) {
		if (assets == null || assets.size() < 1) {
			return;
		}
		
		BigDecimal valueSum = BigDecimal.ZERO;
		BigDecimal targetRatioSum = BigDecimal.ZERO;
		BigDecimal[] targetRatios = new BigDecimal[assets.size()];
		for (int i = 0; i < targetRatios.length; i++) {
			targetRatios[i] = BigDecimal.ZERO;
		}
		try {
			for (int i = 0; i < assets.size(); i++) {
				valueSum = valueSum.add(assets.get(i).getLastValue());
				
				Integer basketId = assets.get(i).getBasketId();
				StringBuffer key = new StringBuffer();
				key.append(QuantumProperties.PROP_PREFIX).append(QuantumProperties.TARGET_RATIO);
				key.append(basketId);
				
				String ratioStr = keyvalService.getKeyvalStr(key.toString());
				if (ratioStr != null) {
					targetRatios[i] = new BigDecimal(ratioStr);
					targetRatioSum = targetRatioSum.add(targetRatios[i]);
				}
			}
			
			// if we have non-zero target ratios
			if (targetRatioSum.abs().doubleValue() >= QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
				for (int i = 0; i < assets.size(); i++) {
					BigDecimal tr = targetRatios[i].divide(targetRatioSum,
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					assets.get(i).setTargetRatio(tr);
					BigDecimal cr = assets.get(i).getLastValue().divide(valueSum,
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					assets.get(i).setCurrentRatio(cr);
					BigDecimal deltaValue = cr.subtract(tr).multiply(valueSum);
					assets.get(i).setRatioDeltaValue(deltaValue);
				}
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Override
	public Asset createAsset(Asset asset) {
		if (asset == null) {
			return null;
		}
		BasketEntity be = new BasketEntity();
		be.setName(asset.getBasketName());
		
		be = basketRepository.save(be);
		Asset result = null;
		if (be != null) {
			result = new Asset(be);
		}
		return result;
	}

	@Override
	public Position getPosition(Integer secId) {
		return getPosition(secId, true);
	}
	
	private Position getPosition(Integer secId, boolean includeTransactions) {
		Security s = getSecurity(secId);
		Position result = Position.EMPTY_POSITION;

		if (s != null) {
			BigDecimal taxRate = BigDecimal.ZERO;
			String taxRateStr = keyvalService.getKeyvalStr(QuantumProperties.PROP_PREFIX + QuantumProperties.TAX_RATE);
			if (taxRateStr != null) {
				try {
					taxRate = new BigDecimal(taxRateStr);
				}
				catch(Exception exc) {
					exc.printStackTrace();
					taxRate = BigDecimal.ZERO;
				}
			}
			
			String symbol = s.getSymbol();
			BigDecimal tPrice = BigDecimal.ZERO;
			BigDecimal principal = BigDecimal.ZERO;
			BigDecimal totalPrincipal = BigDecimal.ZERO;
			BigDecimal shares = BigDecimal.ZERO;
			BigDecimal realizedGain = BigDecimal.ZERO;
			BigDecimal realizedGainYtd = BigDecimal.ZERO;

			Iterable<Transaction> tranIter = transactionService.getTransactionsForSecurity(secId);
			List<Transaction> transactions = new ArrayList<>();
			tranIter.forEach(transactions::add);
			transactions.sort(transactionComparator);
			for (Transaction t : transactions) {
				if (t.getType().equals(QuantumConstants.TRAN_TYPE_BUY)) {
					tPrice = t.getPrice();
					BigDecimal tShares = t.getShares();
					BigDecimal principalAdd = t.getPrice().multiply(tShares);
					principal = principal.add(principalAdd);
					totalPrincipal = totalPrincipal.add(principalAdd);
					shares = shares.add(tShares);
				}
				// using Average Cost Basis for computing cost/profit and deducting from
				// principal
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SELL)) {
					tPrice = t.getPrice();
					BigDecimal averageCostPerShare = principal.divide(shares,
							QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					BigDecimal costOfSharesSold = t.getShares().multiply(averageCostPerShare);
					BigDecimal transactionProfit = (t.getPrice().multiply(t.getShares())).subtract(costOfSharesSold);
					realizedGain = realizedGain.add(transactionProfit);
					// if transaction is in this year, add to realized gain YTD
					if (t.isInCurrentYear()) {
						realizedGainYtd = realizedGainYtd.add(transactionProfit);
					}

					principal = principal.subtract(costOfSharesSold);
					shares = shares.subtract(t.getShares());
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_DIVIDEND)) {
					// do not update tPrice - since we want to use tPrice from previous transaction
					// in the DIV case
					BigDecimal dividend = t.getPrice().multiply(t.getShares());
					realizedGain = realizedGain.add(dividend);
					// if transaction is in this year, add to realized gain YTD
					if (t.isInCurrentYear()) {
						realizedGainYtd = realizedGainYtd.add(dividend);
					}
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_SPLIT)) {
					tPrice = t.getPrice();
					shares = shares.multiply(t.getShares());
				}
				else if (t.getType().equals(QuantumConstants.TRAN_TYPE_CONVERSION)) {
					tPrice = t.getPrice();
					shares = t.getShares();
				}
				// update total shares/value/realizedGain as of this transaction in Transaction
				BigDecimal value = tPrice.multiply(shares);
				t.setTotalShares(shares);
				t.setPrincipal(principal);
				t.setValue(value);
				t.setRealizedGain(realizedGain);
				t.setUnrealizedGain(value.subtract(principal));
			}

			BigDecimal lastStockPrice = BigDecimal.ZERO;
			try {
				lastStockPrice = stockPriceService.getLastStockPrice(symbol);
			} catch (Exception e) {
				System.err.println("Exception in IEXTrading packet: " + e.getMessage());
			}
			BigDecimal lastValue = lastStockPrice.multiply(shares);
			BigDecimal unrealizedGain = lastValue.subtract(principal);

			BigDecimal realizedGainYtdTax = realizedGainYtd.multiply(taxRate);
			
			List<Transaction> transactionList = null;
			if (includeTransactions) {
				transactionList = transactions;
			}
			
			result = new Position(secId, symbol, principal, totalPrincipal, shares, realizedGain, realizedGainYtd,
					realizedGainYtdTax, unrealizedGain, lastStockPrice, lastValue, transactionList);
		}

		return result;
	}

	@Override
	public Iterable<Position> getPositions() {
		List<Position> result = new ArrayList<>();

		Iterable<SecurityEntity> securities = securityRepository.findAll();
		for (SecurityEntity s : securities) {
			Integer secId = s.getId();
			Position p = getPosition(secId, false);
			if (p != Position.EMPTY_POSITION) {
				result.add(p);
			}
		}

		result.sort(positionComparator);
		return result;
	}

	@Override
	public Iterable<Position> getPositions(Integer basketId) {
		List<Position> result = new ArrayList<>();

		Iterable<SecurityEntity> securities = securityRepository.findByBasketId(basketId);
		for (SecurityEntity s : securities) {
			Integer secId = s.getId();
			Position p = getPosition(secId, false);
			if (p != Position.EMPTY_POSITION) {
				result.add(p);
			}
		}

		result.sort(positionComparator);
		return result;
	}

}
