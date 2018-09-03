package bdn.quantum.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bdn.quantum.model.BasketEntity;
import bdn.quantum.repository.util.BasketRowMapper;
import bdn.quantum.repository.util.RepositoryConstants;

@Repository("basketRepository")
public class BasketRepositoryImpl implements BasketRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public List<BasketEntity> getBaskets() {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_BASKET);

		List<BasketEntity> baskets = jdbcTemplate.query(stmtBuf.toString(), new BasketRowMapper());
		return baskets;
	}

	@Override
	@Transactional
	public BasketEntity createBasket(BasketEntity basket) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				StringBuffer stmtBuf = new StringBuffer();
				stmtBuf.append("insert into ");
				stmtBuf.append(RepositoryConstants.TABLE_BASKET);
				stmtBuf.append(" (");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, 1));
				stmtBuf.append(") values (?)");

				PreparedStatement ps = con.prepareStatement(stmtBuf.toString(), new String[] {RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, RepositoryConstants.POS_BASKET_GROUP_ID)});
				ps.setString(RepositoryConstants.POS_BASKET_NAME, basket.getName());
				return ps;
			}
		}, keyHolder);
		
		Number baskeId = keyHolder.getKey();
		return getBasket(baskeId.intValue());
	}
	
	@Transactional
	private BasketEntity getBasket(Integer basketId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_BASKET);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, RepositoryConstants.POS_BASKET_GROUP_ID));
		stmtBuf.append(" = ?");

		BasketEntity b = jdbcTemplate.queryForObject(stmtBuf.toString(), new BasketRowMapper(), basketId);
		return b;
	}

	@Override
	@Transactional
	public void deleteBasket(Integer basketId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("delete from ");
		stmtBuf.append(RepositoryConstants.TABLE_BASKET);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_BASKET, RepositoryConstants.POS_BASKET_GROUP_ID));
		stmtBuf.append(" = ?");

		jdbcTemplate.update(stmtBuf.toString(), basketId);
	}

}
