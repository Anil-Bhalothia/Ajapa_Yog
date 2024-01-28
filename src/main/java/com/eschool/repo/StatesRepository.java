package com.eschool.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.eschool.beans.States;


public interface StatesRepository extends CrudRepository<States, Integer> {
	
	List<States> findByCountryId(Long countryId);

}
