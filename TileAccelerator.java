/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TileAccelerator implements ITickHandler {

	public static final TileAccelerator instance = new TileAccelerator();
	private final HashMap<WeakReference<TileEntity>, Integer> tiles = new HashMap();

	private TileAccelerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private WeakReference<TileEntity> getReferenceFromString(World world, String s) {
		String[] split = s.split("\\:|\\@|,|=|\\[|\\]");
		int dim = Integer.parseInt(split[1]);
		if (dim != world.provider.dimensionId)
			return null;
		int x = Integer.parseInt(split[3]);
		int y = Integer.parseInt(split[4]);
		int z = Integer.parseInt(split[5]);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		return new WeakReference(te);
	}

	private int getValueFromString(String line) {
		String[] s = line.split("=");
		return Integer.parseInt(s[s.length-1]);
	}

	private String getStringFromReference(WeakReference<TileEntity> wf) {
		TileEntity te = wf.get();
		int dim = te.worldObj.provider.dimensionId;
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		return "DIM:"+dim+"@"+"["+x+","+y+","+z+"]"+"="+tiles.get(wf);
	}

	@ForgeSubscribe
	public void save(WorldEvent.Unload evt) {
		if (!evt.world.isRemote) {
			GeoStrata.logger.log("Saving accelerator list for world "+evt.world.provider.dimensionId+".");

			String name = this.getSaveFileName();
			try {
				File dir = new File(this.getSaveFilePath());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File f = new File(this.getFullSavePath());
				if (f.exists())
					f.delete();
				f.createNewFile();
				PrintWriter p = new PrintWriter(f);
				for (WeakReference<TileEntity> wf : tiles.keySet()) {
					TileEntity te = wf.get();
					if (te != null) {
						String line = this.getStringFromReference(wf);
						//ReikaJavaLibrary.pConsole(line+":"+FMLCommonHandler.instance().getEffectiveSide());
						p.append(line+"\n");
					}
				}
				p.close();
			}
			catch (Exception e) {
				GeoStrata.logger.log(e.getMessage()+", and it caused the save to fail!");
				e.printStackTrace();
			}
		}
	}

	@ForgeSubscribe
	public void read(WorldEvent.Load evt) {
		if (!evt.world.isRemote) {
			GeoStrata.logger.log("Loading accelerator list for world "+evt.world.provider.dimensionId+".");

			String name = this.getSaveFileName();
			try {
				BufferedReader p = new BufferedReader(new InputStreamReader(new FileInputStream(this.getFullSavePath())));
				String line = "";
				while (line != null) {
					line = p.readLine();
					if (line != null) {
						WeakReference<TileEntity> wf = this.getReferenceFromString(evt.world, line);
						int count = this.getValueFromString(line);
						//ReikaJavaLibrary.pConsole(line+":"+count+":"+wf+":"+wf.get());
						if (wf != null && wf.get() != null && count > 0) {
							tiles.put(wf, count);
						}
					}
				}
				p.close();
			}
			catch (Exception e) {
				GeoStrata.logger.log(e.getMessage()+", and it caused the read to fail!");
				e.printStackTrace();
			}
		}
	}

	public final String getSaveFileName() {
		return "accelerator.tilemap";
	}

	public final String getSaveFilePath() {
		File save = DimensionManager.getCurrentSaveRootDirectory();
		return save.getPath()+"/GeoStrata/";
	}

	public final String getFullSavePath() {
		return this.getSaveFilePath()+this.getSaveFileName();
	}

	public boolean addTileEntity(TileEntity te, int count) {
		WeakReference<TileEntity> wf = new WeakReference(te);
		if (tiles.containsKey(wf)) {
			int tick = tiles.get(wf);
			if (count > tick) {
				tiles.put(wf, count);
				return true;
			}
			return false;
		}
		else {
			tiles.put(wf, count);
			return true;
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		Iterator<WeakReference<TileEntity>> it = tiles.keySet().iterator();
		while (it.hasNext()) {
			WeakReference<TileEntity> wf = it.next();
			TileEntity te = wf.get();
			if (te != null && !te.isInvalid()) {
				int count = tiles.get(wf);
				for (int i = 0; i < count; i++) {
					te.updateEntity();
				}
			}
			else {
				it.remove();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "TileEntity Update Accelerator";
	}

}
