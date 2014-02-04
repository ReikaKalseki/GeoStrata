/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Guardian;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TileEntityGuardianStone extends TileEntity {

	private final ArrayList<String> extraPlayers = new ArrayList();

	public TileEntityGuardianStone() {

	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < extraPlayers.size(); i++) {
			NBTTagString sg = new NBTTagString("Player"+String.valueOf(i));
			sg.data = extraPlayers.get(i);
			list.appendTag(sg);
			//ReikaJavaLibrary.pConsole(sg.data);
		}
		//ReikaJavaLibrary.pConsole("WRITE:  "+list);
		NBT.setTag("players", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
		NBTTagList list = NBT.getTagList("players");
		//ReikaJavaLibrary.pConsole("READ:  "+list);
		extraPlayers.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagString sg = (NBTTagString)list.tagAt(i);
			extraPlayers.add(sg.data);
		}
	}

	public void addPlayer(String name) {
		if (!extraPlayers.contains(name))
			extraPlayers.add(name);
	}

	public void addPlayer(EntityPlayer ep) {
		this.addPlayer(ep.getEntityName());
	}

	public void removePlayer(String name) {
		extraPlayers.remove(name);
	}

	public void removePlayer(EntityPlayer ep) {
		this.removePlayer(ep.getEntityName());
	}

	public boolean isPlayerInList(String name) {
		return extraPlayers.contains(name);
	}

	public boolean isPlayerInList(EntityPlayer ep) {
		return this.isPlayerInList(ep.getEntityName());
	}

	public List<String> getExtraPlayers() {
		return ReikaJavaLibrary.copyList(extraPlayers);
	}

}
