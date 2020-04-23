package utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static utils.LocalDateTimeUtils.fromString;

public class HttpRequestUtils {
    public static String getQueryParam(Map<String, List<String>> params, String param) {
        return params.get(param).get(0);
    }

    public static Duration getDurationParam(Map<String, List<String>> params, String param) {
        return Duration.parse(getQueryParam(params, param));
    }

    public static LocalDateTime getLocalDateTimeParam(Map<String, List<String>> params, String param) {
        return fromString(getQueryParam(params, param));
    }

    public static long getLongParam(Map<String, List<String>> params, String param) {
        return Long.parseLong(getQueryParam(params, param));
    }

}
