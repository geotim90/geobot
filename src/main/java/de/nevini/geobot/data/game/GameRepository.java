package de.nevini.geobot.data.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Collection<Game> findAllByIdOrNameContainsIgnoreCase(long id, String name);

    Collection<Game> findAllByNameContainsIgnoreCase(String name);

}
