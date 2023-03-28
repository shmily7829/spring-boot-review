package com.mily.springbootreview.respositories;

import com.mily.springbootreview.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String> {

}
