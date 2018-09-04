package bdn.quantum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.model.TranEntity;
import bdn.quantum.repository.TransactionRepository;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public Iterable<TranEntity> getTransactionsForSecurity(Integer secId) {
		return transactionRepository.findBySecId(secId);
	}

	@Override
	public TranEntity getTransaction(Integer id) {
		Optional<TranEntity> t = transactionRepository.findById(id);
		
		TranEntity result = t.get();
		return result;
	}

	@Override
	public TranEntity createTransaction(TranEntity transaction) {
		return transactionRepository.save(transaction);
	}

}
