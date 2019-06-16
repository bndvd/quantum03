package bdn.quantum.repository;

import org.springframework.data.repository.CrudRepository;

import bdn.quantum.model.MarketQuoteEntity;

public interface MarketQuoteRepository extends CrudRepository<MarketQuoteEntity, Long> {

	Iterable<MarketQuoteEntity> findBySymbolOrderByMktDateAsc(String symbol);
	Iterable<MarketQuoteEntity> findBySymbolAndMktDateIsGreaterThanOrderByMktDateAsc(String symbol, String mktDate);
	Iterable<MarketQuoteEntity> findBySymbolAndMktDate(String symbol, String mktDate);
	
}
