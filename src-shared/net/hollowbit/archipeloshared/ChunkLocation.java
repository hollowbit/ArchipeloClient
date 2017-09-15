package net.hollowbit.archipeloshared;

public class ChunkLocation {
	
	public int x;
	public int y;
	
	public ChunkLocation() {
		this.x = 0;
		this.y = 0;
	}

	public ChunkLocation(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChunkLocation))
			return false;
		
		ChunkLocation loc = (ChunkLocation) obj;
		return loc.x == this.x && loc.y == this.y;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + x;
		hash = hash * 31 + y;
		return hash;
	}
	
}
