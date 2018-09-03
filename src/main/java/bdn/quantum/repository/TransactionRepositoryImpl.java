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

import bdn.quantum.model.TranEntity;
import bdn.quantum.repository.util.RepositoryConstants;
import bdn.quantum.repository.util.TransactionRowMapper;

@Repository("transactionRepository")
public class TransactionRepositoryImpl implements TransactionRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public List<TranEntity> getTransactions(Integer secId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_TRANSACTION);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_SEC_ID));
		stmtBuf.append(" = ?");		

		List<TranEntity> transactions = jdbcTemplate.query(stmtBuf.toString(), new TransactionRowMapper(), secId);
		return transactions;
	}

	@Override
	@Transactional
	public TranEntity createTransaction(TranEntity transaction) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				StringBuffer stmtBuf = new StringBuffer();
				stmtBuf.append("insert into ");
				stmtBuf.append(RepositoryConstants.TABLE_TRANSACTION);
				stmtBuf.append(" (");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 1)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 2)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 3)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 4)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 5)).append(", ");
				stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, 6));
				stmtBuf.append(") values (?,?,?,?,?,?)");

				PreparedStatement ps = con.prepareStatement(stmtBuf.toString(), new String[] {RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_ID)});
				ps.setInt(RepositoryConstants.POS_TRANSACTION_SEC_ID, transaction.getSecId());
				ps.setInt(RepositoryConstants.POS_TRANSACTION_USER_ID, transaction.getUserId());
				ps.setDate(RepositoryConstants.POS_TRANSACTION_TRAN_DATE, transaction.getTranDate());
				ps.setString(RepositoryConstants.POS_TRANSACTION_TRAN_TYPE, transaction.getType());
				ps.setDouble(RepositoryConstants.POS_TRANSACTION_TRAN_SHARES, transaction.getShares());
				ps.setDouble(RepositoryConstants.POS_TRANSACTION_TRAN_PRICE, transaction.getPrice());
				return ps;
			}
		}, keyHolder);
		
		Number tranId = keyHolder.getKey();
		return getTransaction(tranId.intValue());
	}

	@Override
	@Transactional
	public TranEntity getTransaction(Integer tranId) {
		StringBuffer stmtBuf = new StringBuffer();
		stmtBuf.append("select * from ");
		stmtBuf.append(RepositoryConstants.TABLE_TRANSACTION);
		stmtBuf.append(" where ");
		stmtBuf.append(RepositoryConstants.getColumnName(RepositoryConstants.TABLE_TRANSACTION, RepositoryConstants.POS_TRANSACTION_TRAN_ID));
		stmtBuf.append(" = ?");

		TranEntity t = jdbcTemplate.queryForObject(stmtBuf.toString(), new TransactionRowMapper(), tranId);
		return t;
	}

}
