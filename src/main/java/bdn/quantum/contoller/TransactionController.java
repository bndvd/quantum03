package bdn.quantum.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bdn.quantum.model.TranEntity;
import bdn.quantum.service.TransactionService;

@RestController("transactionController")
@RequestMapping("api/v1/")
public class TransactionController {
	
	@Autowired
	TransactionService transactionService;

	@RequestMapping(value = "/transactions/{secId}", method = RequestMethod.GET)
	public List<TranEntity> getTransactions(@PathVariable(value="secId") Integer secId) {
		System.out.println("TransactionController.getTransactions: secId=" + secId);
		return transactionService.getTransactions(secId);
	}
	
	@RequestMapping(value = "/transaction/{tranId}", method = RequestMethod.GET)
	public TranEntity getTransaction(@PathVariable(value="tranId") Integer tranId) {
		System.out.println("TransactionController.getTransaction: tranId=" + tranId);
		return transactionService.getTransaction(tranId);
	}

	@RequestMapping(value = "/transaction", method = RequestMethod.POST)
	public TranEntity createTransaction(@RequestBody TranEntity transaction) {
		System.out.println("TransactionController.createTransaction: transaction=" + transaction);
		return transactionService.createTransaction(transaction);
	}

	/*@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ServiceError> handle(RuntimeException exc) {
		ServiceError error = new ServiceError(HttpStatus.OK.value(), exc.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}*/

}
