package bdn.quantum.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.TranEntity;
import bdn.quantum.model.Transaction;
import bdn.quantum.repository.TransactionRepository;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public Iterable<Transaction> getTransactionsForSecurity(Integer secId) {
		Iterable<TranEntity> teIter = transactionRepository.findBySecId(secId);
		
		List<Transaction> result = new ArrayList<>();
		for (TranEntity te : teIter) {
			Transaction t = new Transaction(te);
			result.add(t);
		}
		return result;
	}

	@Override
	public Transaction getTransaction(Integer id) {
		Optional<TranEntity> t = transactionRepository.findById(id);
		
		TranEntity te = t.get();
		Transaction result = null;
		if (te != null) {
			result = new Transaction(te);
		}
		return result;
	}

	@Override
	public Transaction createTransaction(Transaction transaction) {
		// if another transaction exists with the exact timestamp, add 1 sec to new transaction dttm
		Date newDate = (Date) transaction.getTranDate().clone();
		while (transactionRepository.countByTranDate(newDate) > 0) {
			newDate.setTime(newDate.getTime() + QuantumConstants.MILLIS_BETWEEN_TRANSACTIONS_ON_SAME_DATE);
		}
		transaction.setTranDate(newDate);
		
		TranEntity te = new TranEntity(transaction.getId(), transaction.getSecId(), transaction.getUserId(),
				transaction.getTranDate(), transaction.getType(), transaction.getShares(), transaction.getPrice());
		te = transactionRepository.save(te);
		
		Transaction result = null;
		if (te != null) {
			result = new Transaction(te);
		}
		return result;
	}

}
