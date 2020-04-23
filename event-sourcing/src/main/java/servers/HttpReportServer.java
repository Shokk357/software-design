package servers;

import dao.MongoFitnessCenterDao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.EventType;
import model.SubscriptionReport;
import model.TurnstileEvent;
import rx.Observable;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.HttpRequestUtils.*;

public class HttpReportServer implements FitnessCenterServer {
    private final Map<Long, SubscriptionReport> reports = new HashMap<>();

    public HttpReportServer(MongoFitnessCenterDao dao) {
        loadEvents(dao);
    }

    private void loadEvents(MongoFitnessCenterDao dao) {
        dao.getEvents()
                .toSortedList()
                .flatMap(events -> {
                    TurnstileEvent previousEvent = null;
                    for (TurnstileEvent event : events) {
                        if (event.getEventType() == EventType.ENTER) {
                            previousEvent = event;
                        } else if (previousEvent != null) {
                            addVisit(
                                    event.getSubscriptionId(),
                                    Duration.between(previousEvent.getEventTimestamp(), event.getEventTimestamp())
                            );
                        }
                    }
                    return Observable.empty();
                })
                .subscribe();
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        if (path.equals("add_visit")) {
            return addVisit(request.getQueryParameters());
        }
        if (path.equals("get_total_report")) {
            return getTotalReport(request.getQueryParameters());
        }
        if (path.equals("get_subscription_report")) {
            return getSubscriptionReport(request.getQueryParameters());
        }
        return Observable.just("Unsupported request : " + path);
    }

    Observable<String> addVisit(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        Duration visitDuration = getDurationParam(params, "duration");

        addVisit(id, visitDuration);

        return Observable.just("Visit added by subscription with id=" + id + " added");
    }

    private void addVisit(long id, Duration duration) {
        SubscriptionReport report = reports.getOrDefault(id, new SubscriptionReport());
        reports.put(id, report.addVisit(duration));
    }

    Observable<String> getTotalReport(Map<String, List<String>> params) {
        SubscriptionReport totalReport = new SubscriptionReport();
        for (SubscriptionReport report : reports.values()) {
            totalReport = totalReport.mergeReports(report);
        }
        return Observable.just(totalReport.toString());
    }

    Observable<String> getSubscriptionReport(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");

        if (reports.containsKey(id)) {
            return Observable.just(reports.get(id).toString());
        } else {
            return Observable.just("Subscription " + id + " doesn't exist");
        }
    }

}
