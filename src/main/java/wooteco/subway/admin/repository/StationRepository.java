package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.LinkedHashSet;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Query("SELECT * FROM STATION WHERE NAME = :name;")
    Station findByName(@Param("name") String name);

    @Override
    LinkedHashSet<Station> findAllById(Iterable<Long> longs);
}
