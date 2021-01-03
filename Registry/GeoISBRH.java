package Reika.GeoStrata.Registry;

import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Interfaces.Registry.ISBRHEnum;
import Reika.GeoStrata.Rendering.ConnectedStoneRenderer;
import Reika.GeoStrata.Rendering.DecoGenRenderer;
import Reika.GeoStrata.Rendering.GlowVineRenderer;
import Reika.GeoStrata.Rendering.LavaRockRenderer;
import Reika.GeoStrata.Rendering.OreRenderer;
import Reika.GeoStrata.Rendering.VentRenderer;
import Reika.GeoStrata.Rendering.VoidOpalRenderer;

public enum GeoISBRH implements ISBRHEnum {

	connected(ConnectedStoneRenderer.class),
	vent(VentRenderer.class),
	ore(OreRenderer.class),
	lavarock(LavaRockRenderer.class),
	deco(DecoGenRenderer.class),
	vine(GlowVineRenderer.class),
	//shaped(),
	voidopal(VoidOpalRenderer.class);

	private final Class<? extends ISBRH> renderClass;

	private int renderID;
	private ISBRH renderer;

	private static final GeoISBRH[] list = values();

	private GeoISBRH(Class<? extends ISBRH> render) {
		renderClass = render;
	}

	@Override
	public int getRenderID() {
		return renderID;
	}

	@Override
	public ISBRH getRenderer() {
		return renderer;
	}

	@Override
	public void setRenderPass(int pass) {
		renderer.setRenderPass(pass);
	}

	@Override
	public Class<? extends ISBRH> getRenderClass() {
		return renderClass;
	}

	@Override
	public void setRenderID(int id) {
		renderID = id;
	}

	@Override
	public void setRenderer(ISBRH r) {
		renderer = r;
	}

}
