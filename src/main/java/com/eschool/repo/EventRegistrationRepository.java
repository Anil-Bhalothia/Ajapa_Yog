package com.eschool.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.eschool.beans.EventRegistration;
import com.eschool.beans.User;
public interface EventRegistrationRepository  extends CrudRepository<EventRegistration, Integer> {
	EventRegistration findByRegistrationId(int id);
	List<EventRegistration> findAllByFamilyIdAndEventId(int familyId,int eventId);
	List<EventRegistration> findAllByEventIdOrderByFamilyId(int eventId);
	List<EventRegistration> findAllByUserIdOrderByRegistrationIdDesc(int userId);
	List<EventRegistration> findAllByFamilyIdOrderByEventIdDesc(int familyId);
	void deleteByEventIdAndFamilyId(int eventId,int familyId);
	@Query("SELECT er FROM EventRegistration er WHERE er.eventId=:eventId and ((er.arrivalDate<:entryDate and er.departureDate>:entryDate) or (er.arrivalDate=:entryDate and arrivalTime<=:timings) or (er.departureDate=:entryDate and departureTime>=:timings))  order by familyId")
	List<EventRegistration> findAllByEventIdAndEntryDateAndTimingsOrderByFamilyId(@Param("eventId") int eventId,@Param("entryDate") String entryDate,@Param("timings") String timings);
}
