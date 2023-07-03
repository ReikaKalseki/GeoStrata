package Reika.GeoStrata.Blocks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import Reika.DragonAPI.IO.ReikaFileReader;
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

	public static boolean isRenderCenter = false;

	public BlockOreVein(Material mat) {
		super(mat);
		this.setResistance(90);
		this.setHardness(Blocks.stone.blockHardness*2);
		this.setLightOpacity(0);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	public static enum VeinType {
		STONE("Stony", Blocks.stone, 0, Blocks.iron_block),
		ICE("Icy", Blocks.packed_ice, 1, Blocks.diamond_block),
		NETHER("Nether", Blocks.netherrack, 0, Blocks.gold_block),
		END("Mysterious", Blocks.end_stone, 0, Blocks.obsidian);

		public final String displayName;
		public final Block template;
		public final int templateMeta;
		public Block containedBlockIcon;
		private final String defaultBlockIcon;
		private final HashMap<String, Float> defaultItems = new HashMap();
		private final WeightedRandom<HarvestableOre> ores = new WeightedRandom();
		public int maximumHarvestCycles = 0;
		private boolean glowInDark = false;

		private IIcon itemIcon;

		public static final VeinType[] list = values();

		private VeinType(String s, Block b, int m, Block b2) {
			displayName = s;
			template = b;
			templateMeta = m;
			containedBlockIcon = b2;
			defaultBlockIcon = Block.blockRegistry.getNameForObject(b2);
		}

		public boolean isEnabled() {
			return maximumHarvestCycles > 0 && !ores.isEmpty();
		}

		public boolean glow() {
			return this == ICE || glowInDark;
		}

		static {
			STONE.addDefaultItem(Blocks.iron_ore, 10);
			STONE.addDefaultItem(Blocks.gold_ore, 3);
			STONE.addDefaultItem(Items.redstone, 5);
			NETHER.addDefaultItem(Items.gold_nugget, 20);
			NETHER.addDefaultItem(Items.blaze_powder, 5);
			ICE.addDefaultItem(Blocks.ice, 30);
			ICE.addDefaultItem(GeoStrata.lowTempDiamonds, 5);
			END.addDefaultItem(Blocks.obsidian, 25);
			END.addDefaultItem(Items.ender_pearl, 10);
		}

		private void addDefaultItem(Block b, float wt) {
			defaultItems.put(Block.blockRegistry.getNameForObject(b), wt);
		}

		private void addDefaultItem(Item b, float wt) {
			defaultItems.put(Item.itemRegistry.getNameForObject(b), wt);
		}

		private void addDefaultItem(ItemStack b, float wt) {
			defaultItems.put(Item.itemRegistry.getNameForObject(b.getItem())+":"+b.getItemDamage(), wt);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileOreVein();
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return isRenderCenter && VeinType.list[world.getBlockMetadata(x, y, z)].glow() ? 12 : 0;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return VeinType.list[meta].template.getDrops(world, x, y, z, meta, fortune);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return VeinType.list[meta].itemIcon;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		VeinType v = VeinType.list[iba.getBlockMetadata(x, y, z)];
		return v.template.getIcon(s, v.templateMeta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:orevein/base");

		for (VeinType v : VeinType.list) {
			v.itemIcon = ico.registerIcon("geostrata:orevein/"+v.name().toLowerCase(Locale.ENGLISH));
		}
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
		return new ItemStack(this, 1, acc.getMetadata());
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
			TileOreVein tv = (TileOreVein)te;
			VeinType v = tv.getType();
			int left = tv.isInfinite() ? 1 : v.maximumHarvestCycles-tv.harvestsUsed;
			if (left == 0 || v.ores.isEmpty()) {
				tip.add("Depleted");
			}
			else {
				tip.add("Potential Yields: ");
				for (HarvestableOre ore : v.ores.getValues()) {
					tip.add(String.format("%s: %3.2f%%", ore.item.getDisplayName(), v.ores.getProbability(ore)*100));
				}
				if (tv.isInfinite())
					tip.add("Inexhaustible");
				else
					tip.add(left+" items remaining");
			}
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
		else {
			oreData.defaultBlockType = VeinLuaBlock.class;
			ArrayList<String> li = new ArrayList();
			for (VeinType type : VeinType.list) {
				String id = type.name().toLowerCase(Locale.ENGLISH);
				VeinLuaBlock bk = new VeinLuaBlock(id, null, oreData);
				bk.putData("harvestLimit", type == VeinType.NETHER ? 15 : 8-type.ordinal()*2);
				bk.setComment("harvestLimit", "How many items to allow harvesting of before a vein block is depleted.");
				bk.putData("type", id);
				bk.putData("glowInDark", type == VeinType.ICE);
				bk.putData("innerIcon", type.defaultBlockIcon);
				bk.setComment("innerIcon", "The namespaced registry name of the block type to use as the inner-material icon. Optional.");
				VeinLuaBlock items = new VeinLuaBlock("items", bk, oreData);
				for (Entry<String, Float> e : type.defaultItems.entrySet()) {
					VeinLuaBlock item = new VeinLuaBlock("{", items, oreData);
					item.putData("item", e.getKey());
					item.putData("weight", e.getValue());
				}
				oreData.addBlock(id, bk);
				li.addAll(bk.writeToStrings());
			}
			ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
		}

		VeinType.ICE.ores.addEntry(new HarvestableOre(new ItemStack(GeoStrata.lowTempDiamonds), 20), 20);
	}

	private static class VeinLuaBlock extends LuaBlock {

		protected VeinLuaBlock(String n, LuaBlock parent, LuaBlockDatabase db) {
			super(n, parent, db);
			requiredElements.add("maximumHarvestCycles");
			requiredElements.add("items");
		}

	}

	private static void parseOreEntry(String type, LuaBlock b) throws NumberFormatException, IllegalArgumentException, IllegalStateException {
		VeinType vein = VeinType.valueOf(type.toUpperCase(Locale.ENGLISH));
		vein.maximumHarvestCycles = b.getInt("harvestLimit");
		vein.ores.clear();
		vein.glowInDark = b.getBoolean("glowInDark");

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

		public boolean isInfinite() {
			return harvestsUsed < 0;
		}

		public void makeInfinite() {
			harvestsUsed = -1;
		}

		public float getRichness() {
			return 1-Math.max(0, harvestsUsed)/(float)this.getType().maximumHarvestCycles;
		}

		public VeinType getType() {
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
			if (!this.isInfinite())
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
