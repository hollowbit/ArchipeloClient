package net.hollowbit.archipelo.tools.rendering;

import java.util.Comparator;

public class RenderableGameWorldObjectComparator implements Comparator<RenderableGameWorldObject>{

	@Override
	public int compare(RenderableGameWorldObject o1, RenderableGameWorldObject o2) {
		return (int) (o2.getRenderY() - o1.getRenderY());
	}

}
