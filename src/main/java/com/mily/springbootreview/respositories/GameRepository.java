package com.mily.springbootreview.respositories;

import com.mily.springbootreview.data.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String> {

}
