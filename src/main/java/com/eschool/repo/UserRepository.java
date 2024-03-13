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
	User findByFamilyIdAndRole(int familyId,String role);
	User findByMobileNumber(String mobileNumber);
	List<User> findAllByFamilyIdAndIdNotAndStatusAndRoleNot(int familyId,int id,String status,String role);
	List<User> findAllByFamilyIdAndStatusAndRoleNot(int familyId,String status,String role);
	List<User> findAllByStatus(String status);
	User findByCountryCodeAndMobileNumber(String countryCode,String mobileNumber);
	//List<User> findByNameStartingWithAndStatusOrderByFamilyId(String name, String status);    
	@Query("SELECT u FROM User u WHERE u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) and u.role!= :role order by familyId")
    List<User> findByStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber,@Param("role") String role);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) and u.role!= :role order by familyId")
	List<User> findByCountryAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber,@Param("role") String role);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.state=:state and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) and u.role!= :role order by familyId")
	List<User> findByCountryAndStateAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("state") String state,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber,@Param("role") String role);
	@Query("SELECT u FROM User u WHERE u.country=:country and u.state=:state and u.city=:city and u.status=:status and (u.name like :name% or u.email like :email% or u.mobileNumber like :mobileNumber%) and u.role!= :role order by familyId")
	List<User> findByCountryAndStateAndCityAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(@Param("country") String country,@Param("state") String state,@Param("city") String city,@Param("status") String status, @Param("name") String name,@Param("email") String email,@Param("mobileNumber") String mobileNumber,@Param("role") String role);
	@Query("SELECT COUNT(DISTINCT e.familyId) FROM User e")
    Long countDistinctFamilyId();
	@Query("SELECT COUNT(*) FROM User e where e.isDisciple=true")
    Long countDiscipleUsers();	
	@Query("SELECT COUNT(*) FROM User e where e.isDisciple=false")
    Long countNonDiscipleUsers();	
	@Query("SELECT COUNT(*) FROM User e where e.status='Pending'")
    Long countPendingUsers();
	@Query("SELECT COUNT(*) FROM User e where e.status='Rejected'")
    Long countRejectedUsers();
	@Query("SELECT COUNT(*) FROM User e where e.status='Approved'")
    Long countApprovedUsers();
	
}
