package com.eschool.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.eschool.beans.EventRegistration;
public interface EventRegistrationRepository  extends CrudRepository<EventRegistration, Integer> {
	EventRegistration findByRegistrationId(int id);
	List<EventRegistration> findAllByFamilyIdAndEventId(int familyId,int eventId);
	List<EventRegistration> findAllByEventIdOrderByFamilyId(int eventId);
	List<EventRegistration> findAllByUserIdOrderByRegistrationIdDesc(int userId);
	List<EventRegistration> findAllByFamilyIdOrderByEventIdDesc(int familyId);
	void deleteByEventIdAndFamilyId(int eventId,int familyId);
}
