package bdn.quantum.repository.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import bdn.quantum.model.SecurityEntity;

public class SecurityRowMapper implements RowMapper<SecurityEntity> {

	@Override
	public SecurityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		SecurityEntity b = new SecurityEntity(
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_SEC_ID)),
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_BASKET_ID)),
			rs.getString(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_SYMBOL))
		);
		return b;
	}
	
}
