package com.eschool.repo;

import org.springframework.data.repository.CrudRepository;

import com.eschool.beans.Countries;

public interface CountriesRepository extends CrudRepository<Countries, Integer> {

	
}
