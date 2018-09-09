package bdn.quantum.service;

import bdn.quantum.model.Asset;
import bdn.quantum.model.BasketEntity;
import bdn.quantum.model.Position;
import bdn.quantum.model.SecurityEntity;

public interface AssetService {

	public BasketEntity getBasket(Integer id);
	public Iterable<BasketEntity> getBaskets();
	public BasketEntity createBasket(BasketEntity basket);
	public SecurityEntity getSecurity(Integer id);
	public Iterable<SecurityEntity> getSecurities();
	public Iterable<SecurityEntity> getSecuritiesInBasket(Integer basketId);
	public SecurityEntity createSecurity(SecurityEntity security);
	public Asset getAsset(Integer basketId);
	public Iterable<Asset> getAssets();
	public Position getPosition(Integer secId);
	public Iterable<Position> getPositions(Integer basketId);

}
