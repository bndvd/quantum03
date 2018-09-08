package bdn.quantum.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
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
	public TranEntity createTransaction(TranEntity tranEntry) {
		// if another transaction exists with the exact timestamp, add 1 sec to new transaction dttm
		Date newDate = (Date) tranEntry.getTranDate().clone();
		while (transactionRepository.countByTranDate(newDate) > 0) {
			newDate.setTime(newDate.getTime() + QuantumConstants.MILLIS_BETWEEN_TRANSACTIONS_ON_SAME_DATE);
		}
		tranEntry.setTranDate(newDate);
		
		return transactionRepository.save(tranEntry);
	}

}
