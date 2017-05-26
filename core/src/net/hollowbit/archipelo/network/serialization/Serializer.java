package net.hollowbit.archipelo.network.serialization;

import net.hollowbit.archipelo.network.Packet;

public interface Serializer {
	
	public static final String SEPARATOR = ";";
	
	public abstract byte[] serialize(Packet packet);

	public abstract Packet deserialize(byte[] data);
	
}
