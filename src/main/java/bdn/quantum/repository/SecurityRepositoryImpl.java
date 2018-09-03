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

import bdn.quantum.model.SecurityEntity;
import bdn.quantum.repository.util.RepositoryConstants;
import bdn.quantum.repository.util.SecurityRowMapper;

@Repository("securityRepository")
public class SecurityRepositoryImpl implements SecurityRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public List<SecurityEntity> getSecurities() {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_SECURITY);

		List<SecurityEntity> securities = jdbcTemplate.query(stmtBuf.toString(), new SecurityRowMapper());
		return securities;
	}

	@Override
	@Transactional
	public List<SecurityEntity> getSecurities(Integer basketId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_SECURITY);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_BASKET_ID));
		stmtBuf.append(" = ?");

		List<SecurityEntity> securities = jdbcTemplate.query(stmtBuf.toString(), new SecurityRowMapper(), basketId);
		return securities;
	}

	@Override
	@Transactional
	public SecurityEntity createSecurity(SecurityEntity security) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				StringBuffer stmtBuf = new StringBuffer();
				stmtBuf.append("insert into ");
				stmtBuf.append(RepositoryConstants.TABLE_SECURITY);
				stmtBuf.append(" (");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, 1)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, 2));
				stmtBuf.append(") values (?,?)");

				PreparedStatement ps = con.prepareStatement(stmtBuf.toString(), new String[] {RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_SEC_ID)});
				ps.setInt(RepositoryConstants.POS_SECURITY_BASKET_ID, security.getBasketId());
				ps.setString(RepositoryConstants.POS_SECURITY_SYMBOL, security.getSymbol());
				return ps;
			}
		}, keyHolder);
		
		Number secId = keyHolder.getKey();
		return getSecurity(secId.intValue());
	}
	
	@Transactional
	private SecurityEntity getSecurity(Integer secId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_SECURITY);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_SEC_ID));
		stmtBuf.append(" = ?");

		SecurityEntity s = jdbcTemplate.queryForObject(stmtBuf.toString(), new SecurityRowMapper(), secId);
		return s;
	}

	@Override
	@Transactional
	public void deleteSecurity(Integer secId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("delete from ");
		stmtBuf.append(RepositoryConstants.TABLE_SECURITY);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_SECURITY, RepositoryConstants.POS_SECURITY_SEC_ID));
		stmtBuf.append(" = ?");

		jdbcTemplate.update(stmtBuf.toString(), secId);
	}

}
