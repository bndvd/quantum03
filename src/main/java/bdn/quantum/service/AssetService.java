package bdn.quantum.service;

import java.util.List;

import bdn.quantum.model.Asset;
import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.Position;
import bdn.quantum.model.SecurityEntity;

public interface AssetService {

	List<BasketEntity> getBaskets();
	BasketEntity createBasket(BasketEntity basket);
	List<SecurityEntity> getSecurities();
	List<SecurityEntity> getSecurities(Integer basketId);
	SecurityEntity createSecurity(SecurityEntity security);
	List<Asset> getAssets();
	List<Position> getPositions(Integer basketId);

}
