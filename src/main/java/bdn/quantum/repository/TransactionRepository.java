package bdn.quantum.repository;

import java.util.List;

import bdn.quantum.model.TranEntity;

public interface TransactionRepository {

	List<TranEntity> getTransactions(Integer secId);
	TranEntity createTransaction(TranEntity transaction);
	TranEntity getTransaction(Integer tranId);
	
}
