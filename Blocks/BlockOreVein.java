package Reika.GeoStrata.Blocks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Exception.UserErrorException;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Instantiable.IO.LuaBlock.LuaBlockDatabase;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoISBRH;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider", "framesapi.IMoveCheck", "vazkii.botania.api.mana.ILaputaImmobile"})
public class BlockOreVein extends BlockContainer implements IWailaDataProvider {

	public BlockOreVein(Material mat) {
		super(mat);
		this.setLightOpacity(0);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	public static enum VeinType {
		STONE(Blocks.stone, 0, Blocks.iron_block),
		ICE(Blocks.packed_ice, 1, Blocks.diamond_block),
		NETHER(Blocks.netherrack, 0, Blocks.gold_block),
		END(Blocks.end_stone, 0, Blocks.obsidian);

		public final Block template;
		public final int templateMeta;
		public Block containedBlockIcon;
		private final WeightedRandom<HarvestableOre> ores = new WeightedRandom();
		public int maximumHarvestCycles = 0;

		public static final VeinType[] list = values();

		private VeinType(Block b, int m, Block b2) {
			template = b;
			templateMeta = m;
			containedBlockIcon = b2;
		}

		public boolean isEnabled() {
			return maximumHarvestCycles > 0 && !ores.isEmpty();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileOreVein();
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		VeinType v = VeinType.list[iba.getBlockMetadata(x, y, z)];
		return v.template.getIcon(s, v.templateMeta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:orevein");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileOreVein te = (TileOreVein)world.getTileEntity(x, y, z);
		if (te == null)
			return false;
		if (world.isRemote)
			return true;
		ItemStack get = te.tryHarvest();
		if (get != null) {
			ReikaPlayerAPI.addOrDropItem(get, ep);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.pop");
		}
		return true;
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.orevein.getRenderID();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e) {
		return false;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntity te = acc.getTileEntity();
		if (te instanceof TileOreVein) {

		}
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	private static final LuaBlockDatabase oreData = new LuaBlockDatabase();

	public static void loadConfigs() {
		oreData.clear();
		GeoStrata.logger.log("Loading ore vein configs.");
		File f = new File(GeoStrata.config.getConfigFolder(), "GeoStrataOreVeinConfig.lua");
		if (f.exists()) {
			try {
				oreData.loadFromFile(f);
				LuaBlock root = oreData.getRootBlock();
				for (LuaBlock b : root.getChildren()) {
					try {
						String type = b.getString("type");
						GeoStrata.logger.log("Parsing vein type '"+type+"'");
						oreData.addBlock(type, b);
						parseOreEntry(type, b);
					}
					catch (Exception e) {
						GeoStrata.logger.logError("Could not parse config section "+b.getString("type")+": ");
						ReikaJavaLibrary.pConsole(b);
						ReikaJavaLibrary.pConsole("----------------------Cause------------------------");
						e.printStackTrace();
					}
				}
			}
			catch (Exception e) {
				if (e instanceof UserErrorException)
					throw new InstallationException(GeoStrata.instance, "Configs could not be loaded! Correct them and try again.", e);
				else
					throw new RegistrationException(GeoStrata.instance, "Configs could not be loaded! Correct them and try again.", e);
			}

			GeoStrata.logger.log("Configs loaded.");
		}
	}

	private static void parseOreEntry(String type, LuaBlock b) throws NumberFormatException, IllegalArgumentException, IllegalStateException {
		VeinType vein = VeinType.valueOf(type.toUpperCase(Locale.ENGLISH));

		ArrayList<HarvestableOre> blocks = new ArrayList();
		LuaBlock set = b.getChild("items");
		if (set == null)
			throw new IllegalArgumentException("No items specified");

		for (LuaBlock lb : set.getChildren()) {
			String item = lb.getString("item");
			ItemStack find = CustomRecipeList.parseItemString(item, null, true);
			if (find == null) {
				GeoStrata.logger.logError("No such item '"+item+"', skipping");
				continue;
			}
			blocks.add(new HarvestableOre(find, lb.getDouble("weight")));
		}

		if (blocks.isEmpty())
			throw new IllegalArgumentException("No usable items found");

		for (HarvestableOre o : blocks) {
			vein.ores.addEntry(o, o.spawnWeight);
		}

		vein.maximumHarvestCycles = b.getInt("harvestLimit");
		String s = b.getString("innerIcon");
		if (!Strings.isNullOrEmpty(s)) {
			ItemStack find = CustomRecipeList.parseItemString(s, null, true);
			if (find == null) {
				GeoStrata.logger.logError("No such item '"+s+"', skipping icon");
				return;
			}
			Block bk = Block.getBlockFromItem(find.getItem());
			if (bk == null) {
				GeoStrata.logger.logError("No such block for item '"+s+"', skipping icon");
				return;
			}
			vein.containedBlockIcon = bk;
		}
	}

	private static class HarvestableOre {

		private final ItemStack item;
		private final double spawnWeight;

		private HarvestableOre(ItemStack is, double wt) {
			item = is.copy();
			spawnWeight = wt;
		}

	}

	public static class TileOreVein extends TileEntity {

		private int harvestsUsed;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public float getRichness() {
			return 1-(harvestsUsed)/(float)this.getType().maximumHarvestCycles;
		}

		private VeinType getType() {
			return VeinType.list[this.getBlockMetadata()];
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("harvests", harvestsUsed);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			harvestsUsed = NBT.getInteger("harvests");
		}

		public ItemStack tryHarvest() {
			VeinType v = this.getType();
			if (v.ores.isEmpty() || harvestsUsed >= v.maximumHarvestCycles)
				return null;
			harvestsUsed++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return v.ores.getRandomEntry().item.copy();
		}

		@Override
		public final Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}
}
