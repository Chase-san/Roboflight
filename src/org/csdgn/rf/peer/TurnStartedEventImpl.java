package org.csdgn.rf.peer;

import roboflight.events.TurnStartedEvent;

public class TurnStartedEventImpl extends EventImpl implements
		TurnStartedEvent {

	public TurnStartedEventImpl(long time) {
		super(time);
	}

}
