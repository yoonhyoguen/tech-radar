package kr.ac.ync.web.controller;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component("fmt")
public class TimeFormat {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String dt(OffsetDateTime time) {
        return time == null ? "-" : time.atZoneSameInstant(SEOUL).format(DATE_TIME);
    }

    public String ago(OffsetDateTime time) {
        if (time == null) {
            return "기록 없음";
        }
        long minutes = Duration.between(time, OffsetDateTime.now()).toMinutes();
        if (minutes < 1) {
            return "방금 전";
        }
        if (minutes < 60) {
            return minutes + "분 전";
        }
        if (minutes < 60 * 24) {
            return (minutes / 60) + "시간 전";
        }
        return (minutes / (60 * 24)) + "일 전";
    }

    public String elapsed(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) {
            return "-";
        }
        return String.format("%.1f초", Duration.between(from, to).toMillis() / 1000.0);
    }
}
