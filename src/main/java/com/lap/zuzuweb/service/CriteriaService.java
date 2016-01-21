package com.lap.zuzuweb.service;

import java.util.List;

import com.lap.zuzuweb.model.Criteria;

public interface CriteriaService
{
	public List<Criteria> getCriteria(String userID);
	
	public List<Criteria> getAllCriteria();
	
	public String createCriteria(Criteria criteria);
	
	public String updateCriteria(Criteria criteria);
	
	public String deleteCriteria(String criteriaId);
	
	public boolean deleteCriteriaByUser(String userId);
}
