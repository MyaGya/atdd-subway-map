package wooteco.subway.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@Service
public class SectionService {

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    @Autowired
    public SectionService(StationDao stationDao, LineDao lineDao,
        SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public RouteInSection findSectionEndPoint(long lineId) {

        List<RouteInSection> stations = sectionDao.findStationsByLineId(lineId);
        return findEndPoint(stations);
    }

    private RouteInSection findEndPoint(List<RouteInSection> stations) {
        Map<Long, Long> route = new HashMap<>();
        for (RouteInSection routeInSection : stations) {
            route.put(routeInSection.getUpStationId(),
                route.getOrDefault(routeInSection.getUpStationId(), 0L) + 1);

            route.put(routeInSection.getDownStationId(),
                route.getOrDefault(routeInSection.getDownStationId(), 0L) - 1);
        }
        return calcStationInfo(route);
    }

    private RouteInSection calcStationInfo(Map<Long, Long> route) {
        long upStationId = 0L;
        long downStationId = 0L;

        for (Entry<Long, Long> entry : route.entrySet()) {
            if (entry.getValue() == 1L) {
                upStationId = entry.getKey();
            }
            if (entry.getValue() == -1L) {
                downStationId = entry.getKey();
            }
        }
        return new RouteInSection(upStationId, downStationId);
    }


    public List<Station> findStationsInSection(long lineId) {
        RouteInSection sectionEndPoint = findSectionEndPoint(lineId);

        List<Station> stations = new ArrayList<>();

        Map<Long, Long> sectionEndToEndRoute = sectionDao.findStationsByLineId(lineId)
            .stream().collect(Collectors.toMap(RouteInSection::getUpStationId,
                RouteInSection::getDownStationId));

        long targetStationId = sectionEndPoint.getUpStationId();
        while (sectionEndPoint.isNotDownStationId(targetStationId)) {
            stations.add(stationDao.findById(targetStationId));
            targetStationId = sectionEndToEndRoute.get(targetStationId);
        }
        stations.add(stationDao.findById(targetStationId));
        return stations;
    }
}
