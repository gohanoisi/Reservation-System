package client_system;

import java.time.LocalTime;

class TimeRange {
	LocalTime start;
	LocalTime end;

	public TimeRange(LocalTime start, LocalTime end) {
		this.start = start;
		this.end = end;
	}
}