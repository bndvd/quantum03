package bdn.quantum.service;

import bdn.quantum.model.Transaction;

public interface TransactionService {
	
	Iterable<Transaction> getTransactionsForSecurity(Integer secId);
	Transaction getTransaction(Integer id);
	Transaction createTransaction(Transaction tranEntry);
	void deleteTransaction(Integer id);
	
}
