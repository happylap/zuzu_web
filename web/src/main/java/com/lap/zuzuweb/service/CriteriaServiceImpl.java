package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGobject;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.model.Criteria;

public class CriteriaServiceImpl implements CriteriaService
{	

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(CriteriaServiceImpl.class);
	
	private CriteriaDao dao = null;
	
	public CriteriaServiceImpl(CriteriaDao dao)
	{
		this.dao = dao;
	}
	
	@Override
	public List<Criteria> getCriteria(String userID) {
		logger.entering("getCriteria", "{userID: %s}", userID);
		
		return this.dao.getCriteria(userID);
	}

	@Override
	public List<Criteria> getAllCriteria() {
		logger.entering("getAllCriteria");
		
		return this.dao.getAllCriteria();
	}

	@Override
	public List<Criteria> getValidCriteria() {
		logger.entering("getValidCriteria");
		
		return this.dao.getValidCriteria();
	}

	@Override
	public String createCriteria(Criteria criteria) {
		logger.entering("createCriteria", "{criteria: %s}", criteria);
		
		criteria.setCriteria_id(System.currentTimeMillis()+"");
		return this.dao.createCriteria(criteria);
	}

	@Override
	public String updateCriteria(Criteria criteria) {
		logger.entering("updateCriteria", "{criteria: %s}", criteria);
		
		return this.dao.updateCriteria(criteria);
	}

	@Override
	public String deleteCriteria(String userID, String criteriaId) {
		logger.entering("deleteCriteria", "{userID: %s, criteriaId: %s}", userID, criteriaId);
		
		return this.dao.deleteCriteria(criteriaId, userID);
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
		logger.entering("deleteCriteriaByUser", "{userId: %s}", userId);
		
		return this.dao.deleteCriteriaByUser(userId);
	}

	@Override
	public void setEnable(String userID, String criteriaId, boolean enabled) {
		logger.entering("setEnable", "{userID: %s, criteriaId: %s, enabled: %s}", userID, criteriaId, enabled);
		
		Optional<Criteria> existCriteria =  this.getCriteria(userID, criteriaId);
        if (existCriteria != null) {
        	Criteria c =  existCriteria.get();
        	if(c.isEnabled() != enabled){
        		c.setEnabled(enabled);
        		this.dao.updateCriteria(c);
        	}
        }
	}

	@Override
	public void setLastNotifyTime(String userID, String criteriaId, Date lastNotifyTime) {
		logger.entering("setLastNotifyTime", "{userID: %s, criteriaId: %s, lastNotifyTime: %s}", userID, criteriaId, lastNotifyTime);
		
		Optional<Criteria> existCriteria =  this.getCriteria(userID, criteriaId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setLast_notify_time(lastNotifyTime);
        	this.dao.updateCriteria(c);
        }
	}
	
	@Override
    public void setFilters(String userID, String criteriaId, PGobject filters) {
		logger.entering("setFilters", "{userID: %s, criteriaId: %s, filters: %s}", userID, criteriaId, filters);
		
		Optional<Criteria> existCriteria =  this.getCriteria(userID, criteriaId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setFilters(filters);
        	this.dao.updateCriteria(c);
        }
    }

	@Override
	public Optional<Criteria> getCriteria(String userID, String criteriaId) {
		logger.entering("getCriteria", "{userID: %s, criteriaId: %s}", userID, criteriaId);
		
        return this.dao.getCriteria(userID, criteriaId);
	}
	
	public Optional<Criteria> getSingleCriteria(String userID) {
		logger.entering("getSingleCriteria", "{userID: %s}", userID);
		
		return this.dao.getSingleCriteria(userID);
	}
	
}
