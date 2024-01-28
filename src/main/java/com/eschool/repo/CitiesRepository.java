package com.eschool.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eschool.beans.Cities;

public interface CitiesRepository extends JpaRepository<Cities, Integer> {

	 List<Cities> findByStateId(int stateId);
}
