package com.eschool.repo;

import org.springframework.data.repository.CrudRepository;

import com.eschool.beans.OneTimePassword;
import com.eschool.beans.User;

public interface OneTimePasswordRepository  extends CrudRepository<OneTimePassword, Integer>{
	OneTimePassword findByEmail(String email);
	OneTimePassword findByCountryCodeAndMobileNumber(String countryCode,String mobileNumber);
	
}
