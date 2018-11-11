package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.AbstractTransaction;
import bdn.quantum.model.Transaction;
import bdn.quantum.model.qplot.QChart;
import bdn.quantum.model.util.TransactionComparator;
import bdn.quantum.util.DateUtils;
import bdn.quantum.util.PortfolioSimulationException;

@Service("portfolioSimulator")
public class PortfolioSimulator {

	public static final Integer INCR_PRINCIPAL_FREQ_DAILY = Integer.valueOf(1);
	
	@Autowired
	private TransactionComparator transactionComparator;

	
	public PortfolioSimulator() {
	}
	
	//
	// Returns a mapping of symbols to transaction lists, simulating a portfolio
	//
	public HashMap<String, List<AbstractTransaction>> simulate(BigDecimal initPrincipal, BigDecimal incrPrincipal,
			Integer incrFrequency, List<String> symbolList,
			HashMap<String, Iterable<QChart>> symbolToChartChainMap, HashMap<String, BigDecimal> symbolToTargetRatioMap)
			throws PortfolioSimulationException {
		
		if (initPrincipal == null || incrPrincipal == null || incrFrequency == null || symbolList == null ||
				symbolList.isEmpty() || symbolToTargetRatioMap == null ||
				symbolToTargetRatioMap.isEmpty() || symbolToChartChainMap == null || symbolToChartChainMap.isEmpty() ||
				symbolToTargetRatioMap.size() != symbolToChartChainMap.size()) {
			throw new PortfolioSimulationException("Null, empty, or non-matching parameters");
		}
		
		String[] symbols = new String[symbolList.size()];
		for (int i = 0; i < symbols.length; i++) {
			symbols[i] = symbolList.get(i);
		}
		
		// Convert TARGET RATIOS to FRACTIONS OF 1
		BigDecimal sumTargetRatios = BigDecimal.ZERO;
		for (int i = 0; i < symbols.length; i++) {
			BigDecimal nextTR = symbolToTargetRatioMap.get(symbols[i]);
			if (nextTR == null) {
				throw new PortfolioSimulationException("Null target ratio");
			}
			sumTargetRatios = sumTargetRatios.add(nextTR);
		}
		if (sumTargetRatios.abs().doubleValue() < QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
			throw new PortfolioSimulationException("Target ratios add to 0");
		}
		// targets as fractions of 1
		BigDecimal[] targets = new BigDecimal[symbols.length];
		for (int i = 0; i < symbols.length; i++) {
			BigDecimal nextTR = symbolToTargetRatioMap.get(symbols[i]);
			targets[i] = nextTR.divide(sumTargetRatios, QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
		}
		
		// Put Chart iterables into lists
		HashMap<String, List<QChart>> symbolToChartListMap = new HashMap<>();
		for (int i = 0; i < symbols.length; i++) {
			List<QChart> l = new ArrayList<>();
			for (QChart c : symbolToChartChainMap.get(symbols[i])) {
				l.add(c);
			}
			symbolToChartListMap.put(symbols[i], l);
		}
		// make sure all chart lists are of equal lengths
		int chartListLength = symbolToChartListMap.get(symbols[0]).size();
		if (chartListLength < 1) {
			throw new PortfolioSimulationException("Zero chart list length");
		}
		for (int i = 1; i < symbols.length; i++) {
			if (chartListLength != symbolToChartListMap.get(symbols[i]).size()) {
				throw new PortfolioSimulationException("Chart lists of unequal lengths");
			}
		}
		
		int numSecurities = symbols.length;
		HashMap<String, List<AbstractTransaction>> result = new HashMap<>();
		BigDecimal[] shareTallies = new BigDecimal[numSecurities];
		for (int i = 0; i < numSecurities; i++) {
			result.put(symbols[i], new ArrayList<AbstractTransaction>());
			shareTallies[i] = BigDecimal.ZERO;
		}
		
		try {
			Integer id = Integer.valueOf(0);
			
			// Add transactions for initial principal, if non-zero
			if (! BigDecimal.ZERO.equals(initPrincipal)) {
				LocalDate ld = symbolToChartListMap.get(symbols[0]).get(0).getLocalDate();
				Date date = DateUtils.asDate(ld);
				
				for (int i = 0; i < numSecurities; i++) {
					BigDecimal sharePrice = symbolToChartListMap.get(symbols[i]).get(0).getClose();
					BigDecimal value = initPrincipal.multiply(targets[i]);
					BigDecimal sharesToBuy = value.divide(sharePrice, QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
					
					AbstractTransaction t = new Transaction(id, id, id, date, QuantumConstants.TRAN_TYPE_BUY, sharesToBuy, sharePrice);
					result.get(symbols[i]).add(t);
					shareTallies[i] = shareTallies[i].add(sharesToBuy);
				}
			}
			
			if (incrPrincipal.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal[] sharePrices = new BigDecimal[numSecurities];
						
				// Add transactions for incremental principal
				for (int j = 0; j < chartListLength; j++) {
					LocalDate ld = symbolToChartListMap.get(symbols[0]).get(j).getLocalDate();
					
					if (INCR_PRINCIPAL_FREQ_DAILY.equals(incrFrequency)) {
						Date date = DateUtils.asDate(ld);
						for (int i = 0; i < numSecurities; i++) {
							sharePrices[i] = symbolToChartListMap.get(symbols[i]).get(j).getClose();
						}
						
						int indexSecurityToBuy = getIndexOfMaxNegativeTargetRatioDisparity(targets, shareTallies, sharePrices);
						BigDecimal sharesToBuy = incrPrincipal.divide(sharePrices[indexSecurityToBuy], 
								QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP);
						
						AbstractTransaction t = new Transaction(id, id, id, date, QuantumConstants.TRAN_TYPE_BUY, sharesToBuy, sharePrices[indexSecurityToBuy]);
						result.get(symbols[indexSecurityToBuy]).add(t);
						shareTallies[indexSecurityToBuy] = shareTallies[indexSecurityToBuy].add(sharesToBuy);
					}
				}
			}
			
			// sort the transactions
			for (int i = 0; i < numSecurities; i++) {
				result.get(symbols[i]).sort(transactionComparator);
			}
		
		}
		catch (Exception exc) {
			throw new PortfolioSimulationException(exc.getMessage());
		}
		
		return result;
	}
	
	
	//
	// Compares target ratios to computed current ratios and determines which stock/security has the largest
	// negative disparity (below target ratio). This will be used to purchase that security as a form of rebalancing
	// the portfolio towards target ratios.
	//
	private int getIndexOfMaxNegativeTargetRatioDisparity(BigDecimal[] targetRatios, BigDecimal[] shares, BigDecimal[] sharePrices) {
		int result = 0;

		if (targetRatios != null && shares != null && sharePrices != null && targetRatios.length > 0 &&
				targetRatios.length == shares.length && targetRatios.length == sharePrices.length) {
			
			BigDecimal totalValue = BigDecimal.ZERO;
			BigDecimal[] currValues = new BigDecimal[targetRatios.length];
			for (int i = 0; i < currValues.length; i++) {
				currValues[i] = shares[i].multiply(sharePrices[i]);
				totalValue = totalValue.add(currValues[i]);
			}
			
			BigDecimal maxNegativeDisparity = BigDecimal.ZERO;
			for (int i = 0; i < currValues.length; i++) {
				BigDecimal disparity = targetRatios[i].subtract(currValues[i].divide(totalValue,
						QuantumConstants.NUM_DECIMAL_PLACES_PRECISION, RoundingMode.HALF_UP));
				if (disparity.compareTo(maxNegativeDisparity) < 0) {
					maxNegativeDisparity = disparity;
					result = i;
				}
			}
		}
		
		return result;
	}
	
}
