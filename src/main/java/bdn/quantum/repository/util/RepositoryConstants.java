package bdn.quantum.repository.util;

import java.util.HashMap;
import java.util.Map;

public final class RepositoryConstants {

	private static Map<String, Map<Integer, String>> columnMap;

	// Tables
	public static final String TABLE_TRANSACTION = "transaction";
	public static final String TABLE_SECURITY = "security";
	public static final String TABLE_BASKET = "basket";
	
	// transaction table
	public static final Integer POS_TRANSACTION_TRAN_ID = 0;
	public static final Integer POS_TRANSACTION_SEC_ID = 1;
	public static final Integer POS_TRANSACTION_USER_ID = 2;
	public static final Integer POS_TRANSACTION_TRAN_DATE = 3;
	public static final Integer POS_TRANSACTION_TRAN_TYPE = 4;
	public static final Integer POS_TRANSACTION_TRAN_SHARES = 5;
	public static final Integer POS_TRANSACTION_TRAN_PRICE = 6;

	private static final String TRANSACTION_TRAN_ID = "tranId";
	private static final String TRANSACTION_SEC_ID = "secId";
	private static final String TRANSACTION_USER_ID = "userId";
	private static final String TRANSACTION_TRAN_DATE = "tranDate";
	private static final String TRANSACTION_TRAN_TYPE = "type";
	private static final String TRANSACTION_TRAN_SHARES = "shares";
	private static final String TRANSACTION_TRAN_PRICE = "price";
	
	// security table
	public static final Integer POS_SECURITY_SEC_ID = 0;
	public static final Integer POS_SECURITY_BASKET_ID = 1;
	public static final Integer POS_SECURITY_SYMBOL = 2;
	
	private static final String SECURITY_SEC_ID = "secId";
	private static final String SECURITY_BASKET_ID = "basketId";
	private static final String SECURITY_SYMBOL = "symbol";

	// basket table
	public static final Integer POS_BASKET_GROUP_ID = 0;
	public static final Integer POS_BASKET_NAME = 1;
	
	private static final String BASKET_GROUP_ID = "basketId";
	private static final String BASKET_NAME = "name";

	static {
		init();
	}
	
	
	private static void init() {
		columnMap = new HashMap<>();
		
		Map<Integer, String> hm = new HashMap<>();
		hm.put(POS_TRANSACTION_TRAN_ID, TRANSACTION_TRAN_ID);
		hm.put(POS_TRANSACTION_SEC_ID, TRANSACTION_SEC_ID);
		hm.put(POS_TRANSACTION_USER_ID, TRANSACTION_USER_ID);
		hm.put(POS_TRANSACTION_TRAN_DATE, TRANSACTION_TRAN_DATE);
		hm.put(POS_TRANSACTION_TRAN_TYPE, TRANSACTION_TRAN_TYPE);
		hm.put(POS_TRANSACTION_TRAN_SHARES, TRANSACTION_TRAN_SHARES);
		hm.put(POS_TRANSACTION_TRAN_PRICE, TRANSACTION_TRAN_PRICE);
		
		columnMap.put(TABLE_TRANSACTION, hm);
		
		hm = new HashMap<>();
		hm.put(POS_SECURITY_SEC_ID, SECURITY_SEC_ID);
		hm.put(POS_SECURITY_BASKET_ID, SECURITY_BASKET_ID);
		hm.put(POS_SECURITY_SYMBOL, SECURITY_SYMBOL);
		
		columnMap.put(TABLE_SECURITY, hm);
		
		hm = new HashMap<>();
		hm.put(POS_BASKET_GROUP_ID, BASKET_GROUP_ID);
		hm.put(POS_BASKET_NAME, BASKET_NAME);
		
		columnMap.put(TABLE_BASKET, hm);
	}
	
	public static String getColumnName(String table, Integer columnPosition) {
		return columnMap.get(table).get(columnPosition);
	}

}
