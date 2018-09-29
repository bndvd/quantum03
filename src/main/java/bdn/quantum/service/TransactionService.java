package bdn.quantum.service;

import bdn.quantum.model.Transaction;

public interface TransactionService {
	
	public Iterable<Transaction> getTransactionsForSecurity(Integer secId);
	public Transaction getTransaction(Integer id);
	public Transaction createTransaction(Transaction tranEntry);
	public void deleteTransaction(Integer id);
	
}
