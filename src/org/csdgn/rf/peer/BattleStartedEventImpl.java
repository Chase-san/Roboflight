package org.csdgn.rf.peer;

import roboflight.events.BattleStartedEvent;
import roboflight.events.Event;

public class BattleStartedEventImpl extends EventImpl implements
	Event, BattleStartedEvent {

	public BattleStartedEventImpl(long time) {
		super(time);
	}

}
