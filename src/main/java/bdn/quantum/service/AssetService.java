package bdn.quantum.service;

import bdn.quantum.model.Asset;
import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.Position;
import bdn.quantum.model.SecurityEntity;

public interface AssetService {

	Iterable<BasketEntity> getBaskets();
	BasketEntity createBasket(BasketEntity basket);
	SecurityEntity getSecurity(Integer id);
	Iterable<SecurityEntity> getSecurities();
	Iterable<SecurityEntity> getSecuritiesInBasket(Integer basketId);
	SecurityEntity createSecurity(SecurityEntity security);
	Iterable<Asset> getAssets();
	Position getPosition(Integer secId);
	Iterable<Position> getPositions(Integer basketId);

}
