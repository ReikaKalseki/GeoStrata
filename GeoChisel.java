package Reika.GeoStrata;

import net.minecraft.block.Block;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

import com.cricketcraft.chisel.api.carving.CarvableHelper;
import com.cricketcraft.chisel.api.carving.CarvingUtils;
import com.cricketcraft.chisel.api.carving.CarvingUtils.SimpleCarvingGroup;
import com.cricketcraft.chisel.api.carving.CarvingUtils.SimpleCarvingVariation;
import com.cricketcraft.chisel.api.carving.ICarvingGroup;
import com.cricketcraft.chisel.api.carving.ICarvingRegistry;
import com.cricketcraft.chisel.api.carving.ICarvingVariation;


public class GeoChisel {

	public static void loadChiselCompat() {
		ICarvingRegistry chisel = CarvingUtils.getChiselRegistry();
		if (chisel == null) {
			GeoStrata.logger.logError("Could not load Chisel Integration: Chisel's API registries are null!");
		}
		else {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				CarvableHelper cv = new CarvableHelper(rock.getID(RockShapes.SMOOTH));
				ICarvingGroup grp = new SimpleCarvingGroup("GeoStrata_"+rock.getName());
				grp.setOreName("Geo_"+rock.getName());
				grp.setSound(Block.soundTypeStone.soundName);
				chisel.addGroup(grp);
				for (int k = 0; k < RockShapes.shapeList.length; k++) {
					RockShapes s = RockShapes.shapeList[k];
					Block bk = rock.getID(s);
					int meta = rock.getItem(s).getItemDamage();
					ICarvingVariation icv = new SimpleCarvingVariation(bk, meta, k);
					grp.addVariation(icv);
					cv.addVariation(s.name, meta, bk, meta);
				}
			}
		}
	}

}
