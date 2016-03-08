package com.lap.zuzuweb.dao;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.Service;

public interface ServiceDao {
	public Optional<Service> getService(String userID);
	
	public String createService(Service service, List<Purchase> purchases);

	public String updateService(Service service, List<Purchase> purchases);
}
