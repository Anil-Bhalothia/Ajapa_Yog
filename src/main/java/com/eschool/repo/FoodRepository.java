package com.eschool.repo;


import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.eschool.beans.Food;

public interface FoodRepository extends CrudRepository<Food, Integer> {
Food findByEventIdAndEntryDateAndTimings(int eventId,String entryDate,String timings);	
List<Food> findAllByEventId(int eventId);
Food findByFoodId(int foodId);

}
