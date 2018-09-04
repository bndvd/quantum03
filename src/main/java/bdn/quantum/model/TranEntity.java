package bdn.quantum.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import bdn.quantum.QuantumConstants;

@Entity
@Table(name=QuantumConstants.TABLE_TRANSACTION)
public class TranEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name = QuantumConstants.TRANSACTION_SEC_ID)
	private Integer secId;
	@Column(name = QuantumConstants.TRANSACTION_USER_ID)
	private Integer userId;
	@Column(name = QuantumConstants.TRANSACTION_TRAN_DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date tranDate;
	@Column(name = QuantumConstants.TRANSACTION_TRAN_TYPE)
	private String type;
	@Column(name = QuantumConstants.TRANSACTION_TRAN_SHARES)
	private Double shares;
	@Column(name = QuantumConstants.TRANSACTION_TRAN_PRICE)
	private Double price;

	public TranEntity() {
	}

	public TranEntity(Integer id, Integer secId, Integer userId, Date tranDate, String type, Double shares, Double price) {
		this.id = id;
		this.secId = secId;
		this.userId = userId;
		this.tranDate = tranDate;
		this.type = type;
		this.shares = shares;
		this.price = price;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
		strBuf.append("Id:");
		strBuf.append(id);
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
