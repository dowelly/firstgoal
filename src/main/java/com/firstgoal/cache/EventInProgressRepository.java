package com.firstgoal.cache;

import com.firstgoal.data.EventInProgress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventInProgressRepository  extends CrudRepository<EventInProgress, String> {

    Optional<EventInProgress> findByEventUrl(String eventUrl);
}
