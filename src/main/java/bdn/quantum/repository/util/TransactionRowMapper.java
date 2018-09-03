package bdn.quantum.repository.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import bdn.quantum.model.TranEntity;

public class TransactionRowMapper implements RowMapper<TranEntity> {

	@Override
	public TranEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		TranEntity t = new TranEntity(
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_ID)),
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_SEC_ID)),
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_USER_ID)),
			rs.getDate(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_DATE)),
			rs.getString(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_TYPE)),
			rs.getDouble(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_SHARES)),
			rs.getDouble(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_PRICE))
		);
		return t;
	}
	
}
