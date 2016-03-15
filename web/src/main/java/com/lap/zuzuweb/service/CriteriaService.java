package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGobject;

import com.lap.zuzuweb.model.Criteria;

public interface CriteriaService
{
	public Optional<Criteria> getCriteria(String userID, String criteriaId);

	public Optional<Criteria> getSingleCriteria(String userID);
	
	public List<Criteria> getCriteria(String userID);
	
	public List<Criteria> getAllCriteria();

	public List<Criteria> getValidCriteria();
	
	public String createCriteria(Criteria criteria);
	
	public String updateCriteria(Criteria criteria);
	
	public String deleteCriteria(String userID, String criteriaId);
	
	public boolean deleteCriteriaByUser(String userID);
	
	public void setEnable(String userID, String criteriaId, boolean enabled);
	
	public void setLastNotifyTime(String userID, String criteriaId, Date lastNotifyTime);
	
    public void setFilters(String userID, String criteriaId, PGobject filters);
}
