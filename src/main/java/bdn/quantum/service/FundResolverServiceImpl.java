package bdn.quantum.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumProperties;

@Service("findResolverService")
public class FundResolverServiceImpl implements FundResolverService {

	@Autowired
	private KeyvalService keyvalService;
	
	private Map<String, String> hcFundToProxyMap = new HashMap<>();
	private Map<String, BigDecimal> hcFundToFactorMap = new HashMap<>();
	
	
	public FundResolverServiceImpl() {
		initHardcodedProxies();
	}
	
	
	@Override
	public String getStockProxy(String fundSymbol) {
		if (fundSymbol == null || fundSymbol.trim().equals("")) {
			return null;
		}
		
		// check database first
		String property = QuantumProperties.PROP_PREFIX + QuantumProperties.FUND_PROXY + fundSymbol;
		String result = keyvalService.getKeyvalStr(property);
		if (result != null && ! result.trim().equals("")) {
			property = QuantumProperties.PROP_PREFIX + QuantumProperties.FUND_FACTOR + fundSymbol;
			String factor = keyvalService.getKeyvalStr(property);
			if (factor == null || factor.trim().equals("")) {
				result = null;
			}
		}
		
		// check hardcoded values
		if (result == null) {
			result = hcFundToProxyMap.get(fundSymbol);
		}
		
		return result;
	}

	@Override
	public BigDecimal convertProxyToFundValue(String fundSymbol, BigDecimal proxyValue) {
		BigDecimal factor = null;
		
		if (fundSymbol != null && ! fundSymbol.trim().equals("") && proxyValue != null) {
			// check database first
			String property = QuantumProperties.PROP_PREFIX + QuantumProperties.FUND_FACTOR + fundSymbol;
			String factorStr = keyvalService.getKeyvalStr(property);
			if (factorStr != null && ! factorStr.trim().equals("")) {
				try {
					factor = new BigDecimal(factorStr);
				}
				catch (Exception exc) {
					exc.printStackTrace();
					factor = null;
				}
			}
			
			// check hardcoded values
			if (factor == null) {
				factor = hcFundToFactorMap.get(fundSymbol);
			}
		}
		
		BigDecimal result = null;
		if (factor != null) {
			result = proxyValue.multiply(factor);
		}
		
		return result;
	}

	
	private void initHardcodedProxies() {
		// VTI - VTSAX
		hcFundToProxyMap.put("VTSAX", "VTI");
		hcFundToFactorMap.put("VTSAX", new BigDecimal(0.486708868));
		// VBR - VSIAX
		hcFundToProxyMap.put("VSIAX", "VBR");
		hcFundToFactorMap.put("VSIAX", new BigDecimal(0.429572334));
	}
}
