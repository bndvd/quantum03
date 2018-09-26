package bdn.quantum.model;

import java.math.BigDecimal;
import java.util.Date;

//
//Security is a rough representation of TranEntity
//
public class Transaction {

	private Integer id;
	private Integer secId;
	private Integer userId;
	private Date tranDate;
	private String type;
	private BigDecimal shares;
	private BigDecimal price;
	private BigDecimal totalShares = BigDecimal.ZERO;
	private BigDecimal value = BigDecimal.ZERO;
	private BigDecimal realizedGain = BigDecimal.ZERO;
	

	public Transaction(Integer id, Integer secId, Integer userId, Date tranDate, String type, BigDecimal shares,
			BigDecimal price) {
		this.id = id;
		this.secId = secId;
		this.userId = userId;
		this.tranDate = tranDate;
		this.type = type;
		this.shares = shares;
		this.price = price;
	}
	
	public Transaction(TranEntity te) {
		this(te.getId(), te.getSecId(), te.getUserId(), te.getTranDate(), te.getType(), te.getShares(), te.getPrice());
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

	public BigDecimal getShares() {
		return shares;
	}

	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTotalShares() {
		return totalShares;
	}

	public void setTotalShares(BigDecimal totalShares) {
		this.totalShares = totalShares;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getRealizedGain() {
		return realizedGain;
	}

	public void setRealizedGain(BigDecimal realizedGain) {
		this.realizedGain = realizedGain;
	}

}