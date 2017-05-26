package net.hollowbit.archipelo.network.serialization;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class JsonSerializer implements Serializer {
	
	private Json json;
	
	public JsonSerializer() {
		json = new Json();
		json.setOutputType(OutputType.minimal);
	}
	
	
	@Override
	public byte[] serialize(Packet packet) {
		String packetString = json.toJson(packet);
		return (packet.packetType + ";" + packetString).getBytes();
	}

	@Override
	public Packet deserialize(byte[] data) {
		String dataString = new String(data);
		int separatorIndex = dataString.indexOf(SEPARATOR);
		int type = Integer.parseInt(dataString.substring(0, separatorIndex));
		String packetString = dataString.substring(separatorIndex + 1, dataString.length());
		return (Packet) json.fromJson(PacketType.getRegisteredPackets().get(type), packetString);
	}

}
