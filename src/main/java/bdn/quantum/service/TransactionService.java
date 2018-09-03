package bdn.quantum.service;

import java.util.List;

import bdn.quantum.model.TranEntity;

public interface TransactionService {
	
	List<TranEntity> getTransactions(Integer secId);
	TranEntity getTransaction(Integer tranId);
	TranEntity createTransaction(TranEntity transaction);
	
}
