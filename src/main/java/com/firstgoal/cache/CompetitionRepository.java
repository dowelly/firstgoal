package com.firstgoal.cache;

import com.firstgoal.web.Competition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompetitionRepository  extends CrudRepository<Competition, String> {}
