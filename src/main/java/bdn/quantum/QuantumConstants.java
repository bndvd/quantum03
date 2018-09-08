package bdn.quantum;

public final class QuantumConstants {

	public static final String REST_URL_BASE = "api/v1/";
	
	public static final String TABLE_TRANSACTION = "transaction";
	public static final String TABLE_SECURITY = "security";
	public static final String TABLE_BASKET = "basket";
	
	public static final String TRANSACTION_ID = "id";
	public static final String TRANSACTION_SEC_ID = "secId";
	public static final String TRANSACTION_USER_ID = "userId";
	public static final String TRANSACTION_TRAN_DATE = "tranDate";
	public static final String TRANSACTION_TRAN_TYPE = "type";
	public static final String TRANSACTION_TRAN_SHARES = "shares";
	public static final String TRANSACTION_TRAN_PRICE = "price";
	
	public static final String SECURITY_ID = "id";
	public static final String SECURITY_BASKET_ID = "basketId";
	public static final String SECURITY_SYMBOL = "symbol";

	public static final String BASKET_ID = "id";
	public static final String BASKET_NAME = "name";

	// Transaction
	public static final String TRAN_TYPE_BUY = "BUY";
	public static final String TRAN_TYPE_SELL = "SEL";
	public static final String TRAN_TYPE_DIVIDEND = "DIV";
	public static final String TRAN_TYPE_SPLIT = "SPL";
	public static final String TRAN_TYPE_CONVERSION = "CNV";	// fund conversion
	
	// Miscellaneous calculation constants
	public static final int NUM_DECIMAL_PLACES_PRECISION = 20;
	public static final int MILLIS_BETWEEN_TRANSACTIONS_ON_SAME_DATE = 1000;
	
}
