package de.nevini.geobot.data.prefix;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrefixRepository extends CrudRepository<Prefix, Long> {
}
