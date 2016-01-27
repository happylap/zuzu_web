package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.User;

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
	public String deleteCriteria(String criteriaId) {
		return this.dao.deleteCriteria(criteriaId);
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
		return this.dao.deleteCriteriaByUser(userId);
	}

	@Override
	public void setEnable(String criteriaId, String userId, boolean enabled) {
		Criteria c =  this.getCriteria(criteriaId, userId);
        if (c != null && c.isEnabled() != enabled) {
        	c.setEnabled(enabled);
        	this.dao.updateCriteria(c);
        }
	}

	@Override
	public void setLastNotifyTime(String criteriaId, String userId, Date lastNotifyTime) {
		Criteria c =  this.getCriteria(criteriaId, userId);
        if (c != null) {
        	c.setLast_notify_time(lastNotifyTime);
        	this.dao.updateCriteria(c);
        }
	}

	
	private Criteria getCriteria(String criteriaId, String userId){
        Optional<Criteria> existCriteria = this.dao.getCriteria(userId, criteriaId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	return c;
        }
        
        return null;
	}
	
}
