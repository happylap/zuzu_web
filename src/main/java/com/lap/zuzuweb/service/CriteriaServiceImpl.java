package com.lap.zuzuweb.service;

import java.util.List;

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
	public String deleteCriteria(String criteriaId) {
		return this.dao.deleteCriteria(criteriaId);
	}

	@Override
	public boolean deleteCriteriaByUser(String userId) {
		return this.dao.deleteCriteriaByUser(userId);
	}

}
