package bdn.quantum.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TranEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer tranId;
	private Integer secId;
	private Integer userId;
	private Date tranDate;
	private String type;
	private Double shares;
	private Double price;

	public TranEntity() {
	}

	public TranEntity(Integer tranId, Integer secId, Integer userId, Date tranDate, String type, Double shares, Double price) {
		this.tranId = tranId;
		this.secId = secId;
		this.userId = userId;
		this.tranDate = tranDate;
		this.type = type;
		this.shares = shares;
		this.price = price;
	}

	public Integer getTranId() {
		return tranId;
	}

	public void setTranId(Integer tranId) {
		this.tranId = tranId;
	}

	public Integer getSecId() {
		return secId;
	}

	public void setSecId(Integer secId) {
		this.secId = secId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getShares() {
		return shares;
	}

	public void setShares(Double shares) {
		this.shares = shares;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("TranId:");
		strBuf.append(tranId);
		strBuf.append(", SecId:");
		strBuf.append(secId);
		strBuf.append(", Type:");
		strBuf.append(type);
		strBuf.append(", Date:");
		strBuf.append(tranDate);
		strBuf.append(", Shares:");
		strBuf.append(shares);
		strBuf.append(", Price:");
		strBuf.append(price);
		return strBuf.toString();
	}

}
