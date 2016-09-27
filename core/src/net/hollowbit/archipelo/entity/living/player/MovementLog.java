package net.hollowbit.archipelo.entity.living.player;

import java.util.ArrayList;

public class MovementLog {
	
	ArrayList<MovementLogEntry> movementLogEntries;
	
	public MovementLog () {
		movementLogEntries = new ArrayList<MovementLogEntry>();
	}
	
	public synchronized void removeFromBeforeTimeStamp (double timeStamp) {
		ArrayList<MovementLogEntry> movementLogEntriesToRemove = new ArrayList<MovementLogEntry>();
		for (MovementLogEntry logEntry : movementLogEntries) {
			if (logEntry.timeStamp < timeStamp)
				movementLogEntriesToRemove.add(logEntry);
		}
		movementLogEntries.removeAll(movementLogEntriesToRemove);
	}
	
	public synchronized void add (MovementLogEntry logEntry) {
		movementLogEntries.add(logEntry);
	}
	
	public synchronized ArrayList<MovementLogEntry> getCurrentLogs () {
		ArrayList<MovementLogEntry> currentMovementLogEntries = new ArrayList<MovementLogEntry>();
		currentMovementLogEntries.addAll(movementLogEntries);
		return currentMovementLogEntries;
	}
	
}
