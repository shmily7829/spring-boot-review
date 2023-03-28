package com.mily.springbootreview.respositories;

import com.mily.springbootreview.entities.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, String> {


}
