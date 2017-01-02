package azkaban.execregister.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import azkaban.execregister.model.AzkExecutor;

@Repository
public class AzkabanRepository {

	@Autowired
	private JdbcTemplate template;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<AzkExecutor> selectAllExecutors() throws DataAccessException {
		final String SELECT_QUERY = "SELECT id, host, port, active FROM executors";
		return template.query(SELECT_QUERY, new BeanPropertyRowMapper(AzkExecutor.class));
	}
	
	public int insertExecutor(AzkExecutor executor) throws DataAccessException {
		final String INSERT_QUERY = "INSERT INTO executors(host, port, active) values(?,?,?)";
		KeyHolder holder = new GeneratedKeyHolder();
		int ret = template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_QUERY, new String[]{"id"});
				ps.setString(1, executor.getHost());
				ps.setInt(2, executor.getPort());
				ps.setBoolean(3, executor.getActive());
				return ps;
			}
		}, holder);
		executor.setId(holder.getKey().intValue());
		return ret;
	}
	
	public int updateExecutor(AzkExecutor executor) throws DataAccessException {
		final String UPDATE_QUERY = "UPDATE executors SET active=? WHERE id=?";
		int ret = template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(UPDATE_QUERY);
				ps.setBoolean(1, executor.getActive());
				ps.setInt(2, executor.getId());
				return ps;
			}
		});
		return ret;
	}

}
