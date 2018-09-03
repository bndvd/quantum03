package bdn.quantum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.model.TranEntity;
import bdn.quantum.repository.TransactionRepository;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public List<TranEntity> getTransactions(Integer secId) {
		return transactionRepository.getTransactions(secId);
	}

	@Override
	public TranEntity getTransaction(Integer tranId) {
		return transactionRepository.getTransaction(tranId);
	}

	@Override
	public TranEntity createTransaction(TranEntity transaction) {
		return transactionRepository.createTransaction(transaction);
	}

}
