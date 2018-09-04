package bdn.quantum.model;

import java.util.Date;

public class PortfolioData {

	private String version = "1.0";
	private Date lastDate = new Date(0);
	private Iterable<BasketEntity> basketEntities;
	private Iterable<SecurityEntity> securityEntities;
	private Iterable<TranEntity> tranEntities;
	
	public PortfolioData() {}
	
	public PortfolioData(Iterable<BasketEntity> basketEntities, Iterable<SecurityEntity> securityEntities, Iterable<TranEntity> tranEntities) {
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

	public Iterable<BasketEntity> getBasketEntities() {
		return basketEntities;
	}

	public void setBasketEntities(Iterable<BasketEntity> basketEntities) {
		this.basketEntities = basketEntities;
	}

	public Iterable<SecurityEntity> getSecurityEntities() {
		return securityEntities;
	}

	public void setSecurityEntities(Iterable<SecurityEntity> securityEntities) {
		this.securityEntities = securityEntities;
	}

	public Iterable<TranEntity> getTranEntities() {
		return tranEntities;
	}

	public void setTranEntities(Iterable<TranEntity> tranEntities) {
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
