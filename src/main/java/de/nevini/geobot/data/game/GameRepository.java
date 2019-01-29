package de.nevini.geobot.data.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Collection<Game> findAllByIdOrNameContainingIgnoreCase(Long id, String name);

    Collection<Game> findAllByNameContainingIgnoreCase(String name);

}
