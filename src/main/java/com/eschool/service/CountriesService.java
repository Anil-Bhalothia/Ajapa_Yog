package com.eschool.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eschool.beans.Countries;
import com.eschool.repo.CountriesRepository;

@Service
public class CountriesService {
	@Autowired
	private CountriesRepository countryRepository;
    public List<Countries> getAllCountries() {
        return (List<Countries>) countryRepository.findAll();
    }
	
}
