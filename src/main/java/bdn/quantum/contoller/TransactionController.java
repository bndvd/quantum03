package bdn.quantum.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bdn.quantum.QuantumConstants;
import bdn.quantum.model.Transaction;
import bdn.quantum.service.TransactionService;
import bdn.quantum.util.ServiceError;

@RestController("transactionController")
@RequestMapping(QuantumConstants.REST_URL_BASE)
public class TransactionController {
	
	@Autowired
	TransactionService transactionService;

	@RequestMapping(value = "/transactions/{secId}", method = RequestMethod.GET)
	public Iterable<Transaction> getTransactions(@PathVariable(value="secId") Integer secId) {
		System.out.println("TransactionController.getTransactions: secId=" + secId);
		return transactionService.getTransactionsForSecurity(secId);
	}
	
	@RequestMapping(value = "/transaction/{tranId}", method = RequestMethod.GET)
	public Transaction getTransaction(@PathVariable(value="tranId") Integer tranId) {
		System.out.println("TransactionController.getTransaction: tranId=" + tranId);
		return transactionService.getTransaction(tranId);
	}

	@RequestMapping(value = "/transaction", method = RequestMethod.POST)
	public Transaction createTransaction(@RequestBody Transaction transaction) {
		System.out.println("TransactionController.createTransaction: transaction=" + transaction);
		return transactionService.createTransaction(transaction);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ServiceError> handle(RuntimeException exc) {
		ServiceError error = new ServiceError(HttpStatus.OK.value(), exc.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}

}
