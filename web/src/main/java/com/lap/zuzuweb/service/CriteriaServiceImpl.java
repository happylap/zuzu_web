package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGobject;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.model.Criteria;

public class CriteriaServiceImpl implements CriteriaService{
	
	private CriteriaDao dao = null;
	
	public CriteriaServiceImpl(CriteriaDao dao)
	{
		this.dao = dao;
	}
	
	@Override
	public List<Criteria> getCriteria(String userID) {
		return this.dao.getCriteria(userID);
	}

	@Override
	public List<Criteria> getAllCriteria() {
		return this.dao.getAllCriteria();
	}

	@Override
	public String createCriteria(Criteria criteria) {
		criteria.setCriteria_id(System.currentTimeMillis()+"");
		return this.dao.createCriteria(criteria);
	}

	@Override
	public String updateCriteria(Criteria criteria) {
		return this.dao.updateCriteria(criteria);
	}

	@Override
	public String deleteCriteria(String criteriaId, String userId) {
		return this.dao.deleteCriteria(criteriaId, userId);
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
		return this.dao.deleteCriteriaByUser(userId);
	}

	@Override
	public void setEnable(String criteriaId, String userId, boolean enabled) {
		Optional<Criteria> existCriteria =  this.getCriteria(criteriaId, userId);
        if (existCriteria != null) {
        	Criteria c =  existCriteria.get();
        	if(c.isEnabled() != enabled){
        		c.setEnabled(enabled);
        		this.dao.updateCriteria(c);
        	}
        }
	}

	@Override
	public void setLastNotifyTime(String criteriaId, String userId, Date lastNotifyTime) {
		Optional<Criteria> existCriteria =  this.getCriteria(criteriaId, userId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setLast_notify_time(lastNotifyTime);
        	this.dao.updateCriteria(c);
        }
	}
	
	@Override
    public void setFilters(String criteriaId, String userId, PGobject filters) {
		Optional<Criteria> existCriteria =  this.getCriteria(criteriaId, userId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setFilters(filters);
        	this.dao.updateCriteria(c);
        }
    }

	@Override
	public Optional<Criteria> getCriteria(String criteriaId, String userId){
       return this.dao.getCriteria(userId, criteriaId);
	}
	
	public Optional<Criteria> getSingleCriteria(String userID) {
		return this.dao.getSingleCriteria(userID);
	}
	
}
