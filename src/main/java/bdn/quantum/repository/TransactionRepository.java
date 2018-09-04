package bdn.quantum.repository;

import org.springframework.data.repository.CrudRepository;

import bdn.quantum.model.TranEntity;

public interface TransactionRepository extends CrudRepository<TranEntity, Integer> {

	Iterable<TranEntity> findBySecId(Integer secId);
	
}
