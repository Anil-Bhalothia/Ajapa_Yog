package com.eschool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.eschool.beans.Cities;
import com.eschool.beans.Countries;
import com.eschool.beans.States;
import com.eschool.service.CitiesService;
import com.eschool.service.CountriesService;
import com.eschool.service.StatesService;

@RestController
public class LocationController {
	@Autowired
	CitiesService citiesService;
	@Autowired
	CountriesService countriesService;
	@Autowired
	StatesService statesService;
	@GetMapping("/countries")
    public List<Countries> getAllCountries() {
        return countriesService.getAllCountries();
    }
	@GetMapping("/cities/{stateId}")
    public List<Cities> getCitiesByStateId(@PathVariable int stateId) {
        return citiesService.getCitiesByStateId(stateId);
    }
	@GetMapping("/states/{countryId}")
    public List<States> getStatesByCountryId(@PathVariable Long countryId) {
        return statesService.getStatesByCountryId(countryId);
    }
	
}
