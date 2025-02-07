package org.joelson.turf.dailyinc.controller;

import org.joelson.turf.dailyinc.db.DatabaseEntityManager;
import org.joelson.turf.dailyinc.model.RevisitData;
import org.joelson.turf.dailyinc.model.TakeData;
import org.joelson.turf.dailyinc.model.UserData;
import org.joelson.turf.dailyinc.model.VisitData;
import org.joelson.turf.dailyinc.model.ZoneData;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.User;
import org.joelson.turf.turfgame.apiv5.Zone;
import org.joelson.turf.turfgame.apiv5util.FeedsReader;
import org.joelson.turf.util.TimeUtil;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeedImporter {

    private final DatabaseEntityManager entityManager;
    private final ProgressUpdater progressUpdater;

    public FeedImporter(DatabaseEntityManager entityManager, ProgressUpdater progressUpdater) {
        this.entityManager = entityManager;
        this.progressUpdater = progressUpdater;
    }

    public void addVisits(Path path) {
        new FeedsReader().handleFeedObjectFile(path, p -> {}, this::handleTakeover);
    }

    private void handleTakeover(FeedObject feedObject) {
        if (feedObject instanceof FeedTakeover feedTakeover) {
            Zone zone = feedTakeover.getZone();
            User previousOwner = zone.getPreviousOwner();
            User currentOwner = zone.getCurrentOwner();
            User[] assisted = feedTakeover.getAssists();
            ZoneData zoneData = new ZoneData(zone.getId(), zone.getName());
            UserData userData = new UserData(currentOwner.getId(), currentOwner.getName());
            Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
            List<UserData> assists = Collections.emptyList();
            if (assisted != null && assisted.length > 0) {
                assists = Arrays.stream(assisted).map(user -> new UserData(user.getId(), user.getName())).toList();
            }
            List<VisitData> visits;
            if (previousOwner == null || previousOwner.getId() != currentOwner.getId()) {
                visits = entityManager.addTake(new TakeData(zoneData, userData, time), assists);
            } else {
                visits = entityManager.addRevisit(new RevisitData(zoneData, userData, time), assists);
            }
            progressUpdater.updateWithVisits(visits);
        }
    }
}
