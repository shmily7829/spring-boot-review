package com.mily.springbootreview.respositories;

import com.mily.springbootreview.data.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, String> {


}
