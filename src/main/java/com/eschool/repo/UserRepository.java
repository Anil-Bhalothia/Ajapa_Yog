package com.eschool.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.eschool.beans.Event;
import com.eschool.beans.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	User findByEmail(String email);
	User findById(int id);
	User findByMobileNumber(String mobileNumber);
	List<User> findAllByFamilyIdAndIdNotAndStatus(int familyId,int id,String status);
	User findByCountryCodeAndMobileNumber(String countryCode,String mobileNumber);
	//List<User> findByNameStartingWithAndStatusOrderByFamilyId(String name, String status);    
	@Query("SELECT u FROM User u WHERE u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) order by familyId")
    List<User> findByStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) order by familyId")
	List<User> findByCountryAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.state=:state and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) order by familyId")
	List<User> findByCountryAndStateAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("state") String state,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.state=:state and u.city=:city and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) order by familyId")
	List<User> findByCountryAndStateAndCityAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("state") String state,@Param("city") String city,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber);
							
}
