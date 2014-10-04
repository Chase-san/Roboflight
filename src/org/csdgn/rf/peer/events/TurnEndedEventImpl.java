package org.csdgn.rf.peer.events;

import roboflight.events.TurnEndedEvent;

public class TurnEndedEventImpl extends EventImpl implements
		TurnEndedEvent {

	public TurnEndedEventImpl(long time) {
		super(time);
	}

}
