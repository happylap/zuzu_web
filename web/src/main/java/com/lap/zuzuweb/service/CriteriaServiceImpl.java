package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.model.Criteria;

public class CriteriaServiceImpl implements CriteriaService
{	
	private static final Logger logger = LoggerFactory.getLogger(CriteriaServiceImpl.class);
	
	private CriteriaDao dao = null;
	
	public CriteriaServiceImpl(CriteriaDao dao)
	{
		this.dao = dao;
	}
	
	@Override
	public List<Criteria> getCriteria(String userID) {
		logger.info("CriteriaService.getCriteria: " + userID);
		return this.dao.getCriteria(userID);
	}

	@Override
	public List<Criteria> getAllCriteria() {
		logger.info("CriteriaService.getAllCriteria");
		return this.dao.getAllCriteria();
	}

	@Override
	public List<Criteria> getValidCriteria() {
		logger.info("CriteriaService.getValidCriteria");
		return this.dao.getValidCriteria();
	}

	@Override
	public String createCriteria(Criteria criteria) {
		logger.info("CriteriaService.createCriteria: " + criteria);
		criteria.setCriteria_id(System.currentTimeMillis()+"");
		return this.dao.createCriteria(criteria);
	}

	@Override
	public String updateCriteria(Criteria criteria) {
		logger.info("CriteriaService.updateCriteria: " + criteria);
		return this.dao.updateCriteria(criteria);
	}

	@Override
	public String deleteCriteria(String userID, String criteriaId) {
		logger.info("CriteriaService.deleteCriteria: " + userID + ", " + criteriaId);
		return this.dao.deleteCriteria(criteriaId, userID);
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
		logger.info("CriteriaService.deleteCriteriaByUser: " + userId);
		return this.dao.deleteCriteriaByUser(userId);
	}

	@Override
	public void setEnable(String userID, String criteriaId, boolean enabled) {
		logger.info("CriteriaService.setEnable: " + userID + ", " + criteriaId + ", " + enabled);
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
		logger.info("CriteriaService.setLastNotifyTime: " + userID + ", " + criteriaId + ", " + lastNotifyTime);
		Optional<Criteria> existCriteria =  this.getCriteria(userID, criteriaId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setLast_notify_time(lastNotifyTime);
        	this.dao.updateCriteria(c);
        }
	}
	
	@Override
    public void setFilters(String userID, String criteriaId, PGobject filters) {
		logger.info("CriteriaService.setFilters: " + userID + ", " + criteriaId + ", " + filters);
		Optional<Criteria> existCriteria =  this.getCriteria(userID, criteriaId);
        if (existCriteria.isPresent()) {
        	Criteria c =  existCriteria.get();
        	c.setFilters(filters);
        	this.dao.updateCriteria(c);
        }
    }

	@Override
	public Optional<Criteria> getCriteria(String userID, String criteriaId){
		logger.info("CriteriaService.getCriteria: " + userID + ", " + criteriaId);
       return this.dao.getCriteria(userID, criteriaId);
	}
	
	public Optional<Criteria> getSingleCriteria(String userID) {
		logger.info("CriteriaService.getSingleCriteria: " + userID);
		return this.dao.getSingleCriteria(userID);
	}
	
}
