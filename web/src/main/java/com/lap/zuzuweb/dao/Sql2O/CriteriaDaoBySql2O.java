package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.model.Criteria;

public class CriteriaDaoBySql2O extends AbstratcDaoBySql2O implements CriteriaDao {

	static private String SQL_GET_CRITERIA = "SELECT criteria_id, user_id, enabled, expire_time, apple_product_id,"
			+ " last_notify_time, filters FROM \"Criteria\"";
	
	static private String SQL_GET_CRITERIA_BY_USER = "SELECT criteria_id, user_id, enabled, expire_time, apple_product_id, "
			+ " last_notify_time, filters FROM \"Criteria\" WHERE user_id=:user_id";
	
	static private String SQL_CREATE_CRITERIA = "INSERT INTO \"Criteria\"(criteria_id, user_id, enabled, expire_time, "
			+ " apple_product_id, last_notify_time, filters) "
			+ " VALUES (:criteria_id, :user_id, :enabled, :expire_time, :apple_product_id, :last_notify_time, :filters)";

	static private String SQL_UPDATE_CRITERIA = "UPDATE \"Criteria\" SET enabled=:enabled, expire_time=:expire_time, "
			+ " apple_product_id=:apple_product_id, last_notify_time, filters=:filters "
			+ " WHERE criteria_id=:criteria_id AND user_id=:user_id";
	
	static private String SQL_REMOVE_CRITERIA = "DELETE FROM \"Criteria\" Where criteria_id=:criteria_id";
	
	static private String SQL_REMOVE_CRITERIA_BY_USER = "DELETE FROM \"Criteria\" Where user_id=:user_id";
	
	@Override
	public List<Criteria> getCriteria(String userID) {
        try (Connection conn = sql2o.open()) {
            List<Criteria> criteria = conn.createQuery(SQL_GET_CRITERIA_BY_USER)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Criteria.class);
            return criteria;
        }
	}

	@Override
	public List<Criteria> getAllCriteria() {
        try (Connection conn = sql2o.open()) {
            List<Criteria> criteria = conn.createQuery(SQL_GET_CRITERIA)
                    .executeAndFetch(Criteria.class);
            return criteria;
        }
	}

	@Override
	public String createCriteria(Criteria criteria) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_CREATE_CRITERIA)
            		.addParameter("criteria_id", criteria.getCriteria_id())
            		.addParameter("user_id", criteria.getUser_id())
            		.addParameter("enabled", criteria.isEnabled())
                    .addParameter("expire_time", criteria.getExpire_time())
                    .addParameter("apple_product_id", criteria.getApple_product_id())
                    .addParameter("last_notify_time", criteria.getLast_notify_time())
                    .addParameter("filters", criteria.getFilters())
                    .executeUpdate();
            conn.commit();
            return criteria.getCriteria_id();
        }
	}

	@Override
	public String updateCriteria(Criteria criteria) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_CRITERIA)
            		.addParameter("criteria_id", criteria.getCriteria_id())
            		.addParameter("user_id", criteria.getUser_id())
            		.addParameter("enabled", criteria.isEnabled())
                    .addParameter("expire_time", criteria.getExpire_time())
                    .addParameter("apple_product_id", criteria.getApple_product_id())
                    .addParameter("last_notify_time", criteria.getLast_notify_time())
                    .addParameter("filters", criteria.getFilters())
                    .executeUpdate();
            conn.commit();
            return criteria.getCriteria_id();
        }
	}

	@Override
	public String deleteCriteria(String criteriaId) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_CRITERIA)
            		.addParameter("criteria_id", criteriaId)
                    .executeUpdate();
            conn.commit();
            return criteriaId;
        }
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_CRITERIA_BY_USER)
            		.addParameter("user_id", userId)
                    .executeUpdate();
            conn.commit();
            return true;
        }
	}

}
