package com.eschool.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eschool.beans.Cities;
import com.eschool.repo.CitiesRepository;

@Service
public class CitiesService {
	@Autowired
	private CitiesRepository citiesRepository;
	public List<Cities> getCitiesByStateId(int stateId) {
        return citiesRepository.findByStateId(stateId); // Use findByState_id
    }

}
