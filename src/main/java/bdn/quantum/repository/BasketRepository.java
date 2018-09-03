package bdn.quantum.repository;

import java.util.List;

import bdn.quantum.model.BasketEntity;

public interface BasketRepository {

	List<BasketEntity> getBaskets();
	BasketEntity createBasket(BasketEntity basket);
	void deleteBasket(Integer basketId);
	
}
