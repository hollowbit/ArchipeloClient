package net.hollowbit.archipelo.entity.living.player;

import java.util.LinkedList;

import net.hollowbit.archipelo.network.packets.ControlsPacket;

public class MovementLog {
	
	LinkedList<ControlsPacket> commandEntries;
	
	public MovementLog () {
		commandEntries = new LinkedList<ControlsPacket>();
	}
	
	public synchronized void removeCommandsOlderThan (int id) {
		LinkedList<ControlsPacket> commandEntriesToRemove = new LinkedList<ControlsPacket>();
		for (ControlsPacket logEntry : commandEntries) {
			if (logEntry.id <= id)
				commandEntriesToRemove.add(logEntry);
		}
		commandEntries.removeAll(commandEntriesToRemove);
	}
	
	public synchronized void add (ControlsPacket logEntry) {
		commandEntries.add(logEntry);
	}
	
	public synchronized LinkedList<ControlsPacket> getCurrentlyStoredCommands () {
		LinkedList<ControlsPacket> currentCommands = new LinkedList<ControlsPacket>();
		currentCommands.addAll(commandEntries);
		return currentCommands;
	}
	
	public synchronized ControlsPacket getCommandById (int id) {
		for (ControlsPacket command : commandEntries) {
			if (command.id == id)
				return command;
		}
		
		return null;
	}
	
}
