package com.eschool.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.eschool.beans.GroupSMS;
public interface GroupSMSRepo extends CrudRepository<GroupSMS, Integer>{
	List<GroupSMS> findAll();
}
