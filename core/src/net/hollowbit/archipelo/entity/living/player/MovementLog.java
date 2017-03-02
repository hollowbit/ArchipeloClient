package net.hollowbit.archipelo.entity.living.player;

import java.util.ArrayList;

import net.hollowbit.archipelo.network.packets.ControlsPacket;

public class MovementLog {
	
	ArrayList<ControlsPacket> commandEntries;
	
	public MovementLog () {
		commandEntries = new ArrayList<ControlsPacket>();
	}
	
	public synchronized void removeCommandsOlderThan (long timeStamp) {
		ArrayList<ControlsPacket> commandEntriesToRemove = new ArrayList<ControlsPacket>();
		for (ControlsPacket logEntry : commandEntries) {
			if (logEntry.timeStamp < timeStamp)
				commandEntriesToRemove.add(logEntry);
		}
		commandEntries.removeAll(commandEntriesToRemove);
	}
	
	public synchronized void add (ControlsPacket logEntry) {
		commandEntries.add(logEntry);
	}
	
	public synchronized ArrayList<ControlsPacket> getCurrentlyStoredCommands () {
		ArrayList<ControlsPacket> currentCommands = new ArrayList<ControlsPacket>();
		currentCommands.addAll(commandEntries);
		return currentCommands;
	}
	
}
