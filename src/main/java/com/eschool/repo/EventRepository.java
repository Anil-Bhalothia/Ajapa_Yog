package com.eschool.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.eschool.beans.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {

	@Query("select max(e.eventId) from Event e")
    Integer findMaxEventIdValue();
	
	List<Event> findByEventNameStartingWithAndEventStatusAndBookingStatusOrderByEventDateDesc(String eventName, boolean eventStatus,boolean bookingStatus);
	Event findByEventId(int eventId);
}
