package bdn.quantum.service;

import bdn.quantum.model.TranEntity;

public interface TransactionService {
	
	Iterable<TranEntity> getTransactionsForSecurity(Integer secId);
	TranEntity getTransaction(Integer id);
	TranEntity createTransaction(TranEntity transaction);
	
}
