package com.eschool.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eschool.beans.States;
import com.eschool.repo.StatesRepository;

@Service
public class StatesService {
	@Autowired
	 private StatesRepository statesRepository;
     public List<States> getStatesByCountryId(Long countryId) {
        return statesRepository.findByCountryId(countryId);
    }
	
}
