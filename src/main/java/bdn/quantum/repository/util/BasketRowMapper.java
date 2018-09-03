package bdn.quantum.repository.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import bdn.quantum.model.BasketEntity;

public class BasketRowMapper implements RowMapper<BasketEntity> {

	@Override
	public BasketEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		BasketEntity b = new BasketEntity(
			rs.getInt(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, RepositoryConstants.POS_BASKET_GROUP_ID)),
			rs.getString(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, RepositoryConstants.POS_BASKET_NAME))
		);
		return b;
	}
	
}
