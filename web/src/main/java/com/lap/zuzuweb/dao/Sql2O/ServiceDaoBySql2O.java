/**
 * 
 */
package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.util.CommonUtils;

/**
 * @author eechih
 *
 */
public class ServiceDaoBySql2O extends AbstratcDaoBySql2O implements ServiceDao {

	static private String SQL_GET_SERVICE_BY_USER = "SELECT user_id, expire_time, update_time"
			+ " FROM \"ZuzuService\" WHERE user_id=:user_id";

	static private String SQL_CREATE_SERVICE = "INSERT INTO \"ZuzuService\"(user_id, expire_time, update_time) "
			+ " VALUES (:user_id, :expire_time, :update_time)";

	static private String SQL_UPDATE_SERVICE = "UPDATE \"ZuzuService\" SET expire_time=:expire_time, update_time=:update_time"
			+ " WHERE user_id=:user_id";

	static private String SQL_UPDATE_PURCHASE = "UPDATE \"ZuzuPurchase\" SET is_valid=:is_valid WHERE purchase_id=:purchase_id";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lap.zuzuweb.dao.ServiceDao#getService(java.lang.String)
	 */
	@Override
	public Optional<Service> getService(String userID) {
		try (Connection conn = sql2o.open()) {
			List<Service> service = conn.createQuery(SQL_GET_SERVICE_BY_USER)
					.addParameter("user_id", userID)
					.executeAndFetch(Service.class);
			if (service.size() == 0) {
				return Optional.empty();
			} else if (service.size() >= 1) {
				return Optional.of(service.get(0));
			} else {
				throw new RuntimeException();
			}
		}
	}
	
	public String createService(Service service, List<Purchase> purchases) {
		try (Connection conn = sql2o.beginTransaction()) {
			if (service.getUpdate_time() == null) {
				service.setUpdate_time(CommonUtils.getUTCNow());
			}
			
            conn.createQuery(SQL_CREATE_SERVICE)
            		.addParameter("user_id", service.getUser_id())
            		.addParameter("expire_time", service.getExpire_time())
            		.addParameter("update_time", service.getUpdate_time())
                    .executeUpdate();
            
            for (Purchase purchase: purchases) {
            	conn.createQuery(SQL_UPDATE_PURCHASE)
					.addParameter("purchase_id", purchase.getPurchase_id())
					.addParameter("is_valid", purchase.is_valid())
					.executeUpdate();
            }
            
            conn.commit();
            return service.getUser_id();
        }
	}
	
	public String updateService(Service service, List<Purchase> purchases) {
		try (Connection conn = sql2o.beginTransaction()) {
			if (service.getUpdate_time() == null) {
				service.setUpdate_time(CommonUtils.getUTCNow());
			}
			
            conn.createQuery(SQL_UPDATE_SERVICE)
            		.addParameter("user_id", service.getUser_id())
            		.addParameter("expire_time", service.getExpire_time())
            		.addParameter("update_time", service.getUpdate_time())
                    .executeUpdate();
            
            for (Purchase purchase: purchases) {
            	conn.createQuery(SQL_UPDATE_PURCHASE)
					.addParameter("purchase_id", purchase.getPurchase_id())
					.addParameter("is_valid", purchase.is_valid())
					.executeUpdate();
            }
            
            conn.commit();
            return service.getUser_id();
        }
	}

}
