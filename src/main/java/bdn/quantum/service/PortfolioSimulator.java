package bdn.quantum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Position;
import bdn.quantum.model.qplot.QChart;

@Service("portfolioSimulator")
public class PortfolioSimulator {

	
	public PortfolioSimulator() {
	}
	
	
	public Iterable<Position> simulate(BigDecimal initPrincipal, BigDecimal incrPrincipal,
			HashMap<String, Iterable<QChart>> symbolToChartChainMap, HashMap<String, BigDecimal> symbolToTargetRatioMap) {
		
		if (initPrincipal == null || incrPrincipal == null || symbolToTargetRatioMap == null ||
				symbolToTargetRatioMap.isEmpty() || symbolToChartChainMap == null || symbolToChartChainMap.isEmpty() ||
				symbolToTargetRatioMap.size() != symbolToChartChainMap.size()) {
			return null;
		}
		
		Set<String> symbolSet = symbolToChartChainMap.keySet();
		String[] symbols = (String[]) symbolSet.toArray();
		
		// Convert TARGET RATIOS to FRACTIONS OF 1
		BigDecimal sumTargetRatios = BigDecimal.ZERO;
		for (int i = 0; i < symbols.length; i++) {
			BigDecimal nextTR = symbolToTargetRatioMap.get(symbols[i]);
			if (nextTR == null) {
				return null;
			}
			sumTargetRatios = sumTargetRatios.add(nextTR);
		}
		if (sumTargetRatios.abs().doubleValue() < QuantumConstants.THRESHOLD_DECIMAL_EQUALING_ZERO) {
			return null;
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
		for (int i = 1; i < symbols.length; i++) {
			if (chartListLength != symbolToChartListMap.get(symbols[i]).size()) {
				return null;
			}
		}
		
	}
	
	
}
