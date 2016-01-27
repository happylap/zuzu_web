package com.lap.zuzuweb.dao;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Criteria;

public interface CriteriaDao 
{
	public List<Criteria> getCriteria(String userID);
	
	public Optional<Criteria> getCriteria(String userID, String criteria_id);
	
	public List<Criteria> getAllCriteria();
	
	public String createCriteria(Criteria criteria);
	
	public String updateCriteria(Criteria criteria);
	
	public String deleteCriteria(String criteriaId);
	
	public boolean deleteCriteriaByUser(String userId);
}
