package bdn.quantum.model;

import java.sql.Date;
import java.util.List;

public class PortfolioData {

	private String version = "1.0";
	private Date lastDate = new Date(0);
	private List<BasketEntity> basketEntities;
	private List<SecurityEntity> securityEntities;
	private List<TranEntity> tranEntities;
	
	public PortfolioData() {}
	
	public PortfolioData(List<BasketEntity> basketEntities, List<SecurityEntity> securityEntities, List<TranEntity> tranEntities) {
		setBasketEntities(basketEntities);
		setSecurityEntities(securityEntities);
		setTranEntities(tranEntities);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public List<BasketEntity> getBasketEntities() {
		return basketEntities;
	}

	public void setBasketEntities(List<BasketEntity> basketEntities) {
		this.basketEntities = basketEntities;
	}

	public List<SecurityEntity> getSecurityEntities() {
		return securityEntities;
	}

	public void setSecurityEntities(List<SecurityEntity> securityEntities) {
		this.securityEntities = securityEntities;
	}

	public List<TranEntity> getTranEntities() {
		return tranEntities;
	}

	public void setTranEntities(List<TranEntity> tranEntities) {
		this.tranEntities = tranEntities;
		computeLastDate();
	}
	
	private void computeLastDate() {
		if (tranEntities != null) {
			for(TranEntity t : tranEntities) {
				Date td = t.getTranDate();
				if (lastDate.before(td)) {
					lastDate = td;
				}
			}
		}
	}
}
