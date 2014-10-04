package org.csdgn.rf.peer.events;

import roboflight.events.Event;

public class EventImpl implements Event {

	public final long time;

	public EventImpl(long time) {
		this.time = time;
	}

	@Override
	public long getTime() {
		return time;
	}

}
