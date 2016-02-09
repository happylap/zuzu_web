package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGobject;

import com.lap.zuzuweb.model.Criteria;

public interface CriteriaService
{
	public Optional<Criteria> getCriteria(String criteriaId,String userID);

	public Optional<Criteria> getSingleCriteria(String userID);
	
	public List<Criteria> getCriteria(String userID);
	
	public List<Criteria> getAllCriteria();
	
	public String createCriteria(Criteria criteria);
	
	public String updateCriteria(Criteria criteria);
	
	public String deleteCriteria(String criteriaId, String userId);
	
	public boolean deleteCriteriaByUser(String userId);
	
	public void setEnable(String criteriaId, String userId, boolean enabled);
	
	public void setLastNotifyTime(String criteriaId, String userId, Date lastNotifyTime);
	
    public void setFilters(String criteriaId, String userId, PGobject filters);
}
