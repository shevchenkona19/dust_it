package dustit.clientapp.utils;

public class TimeTracking {
    private static TimeTracking timeTracking;

    private TimeTracking() {}

    public static TimeTracking getInstance() {
        if (timeTracking == null) timeTracking = new TimeTracking();
        return timeTracking;
    }

    private long startDate;

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getStartDate() {
        return startDate;
    }
}
