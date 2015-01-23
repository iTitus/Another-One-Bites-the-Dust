package ganymedes01.aobd.recipes.modules;

import ganymedes01.aobd.lib.CompatType;
import ganymedes01.aobd.lib.Reference;
import ganymedes01.aobd.ore.Ore;
import ganymedes01.aobd.recipes.RecipesModule;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TinkersConstructModule extends RecipesModule {

	@SideOnly(Side.CLIENT)
	private static IIcon still, flow;

	public TinkersConstructModule() {
		super(CompatType.TINKERS_CONSTRUCT, "iron", "gold", "aluminium", "cobalt", "ardite", "platinum", "nickel", "silver", "lead", "copper", "tin", "steel", "endium");
	}

	@Override
	public void initOre(Ore ore) {
		Fluid fluid = new MoltenMetal(ore);
		FluidRegistry.registerFluid(fluid);

		int temp = (int) ore.energy(600);

		ItemStack block;
		try {
			block = getOreStack("block", ore);
		} catch (NullPointerException e) {
			block = null;
		}

		// Block
		if (block != null) {
			Smeltery.addMelting(block, temp, new FluidStack(fluid, TConstruct.blockLiquidValue));
			TConstructRegistry.getBasinCasting().addCastingRecipe(block, new FluidStack(fluid, TConstruct.blockLiquidValue), 50);
		} else
			block = new ItemStack(Blocks.iron_block); // Just for looks

		// Ore
		ItemStack oreBlock = getOreStack("ore", ore);
		if (oreBlock.getItem() instanceof ItemBlock)
			Smeltery.addMelting(oreBlock, temp, new FluidStack(fluid, TConstruct.oreLiquidValue));
		else
			Smeltery.addMelting(oreBlock, Block.getBlockFromItem(block.getItem()), block.getItemDamage(), temp, new FluidStack(fluid, TConstruct.oreLiquidValue));

		// Ingot
		Smeltery.addMelting(getOreStack("ingot", ore), Block.getBlockFromItem(block.getItem()), block.getItemDamage(), temp, new FluidStack(fluid, TConstruct.ingotLiquidValue));
		TConstructRegistry.getTableCasting().addCastingRecipe(getOreStack("ingot", ore), new FluidStack(fluid, TConstruct.ingotLiquidValue), new ItemStack(TinkerSmeltery.metalPattern), 50);

		// Others
		tryAddRecipeForItem("nugget", ore, block, fluid, temp, TConstruct.nuggetLiquidValue);
		tryAddRecipeForItem("dust", ore, block, fluid, temp, TConstruct.ingotLiquidValue);
	}

	private void tryAddRecipeForItem(String prefix, Ore ore, ItemStack block, Fluid fluid, int temp, int fluidAmount) {
		try {
			Smeltery.addMelting(getOreStack(prefix, ore), Block.getBlockFromItem(block.getItem()), block.getItemDamage(), temp, new FluidStack(fluid, fluidAmount));
		} catch (NullPointerException e) {
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerIcons(TextureMap map) {
		still = map.registerIcon(Reference.MOD_ID + ":fluid_still");
		flow = map.registerIcon(Reference.MOD_ID + ":fluid_flow");
	}

	private static class MoltenMetal extends Fluid {

		private final Ore ore;

		public MoltenMetal(Ore ore) {
			super(ore.name().toLowerCase());
			this.ore = ore;
		}

		@Override
		public IIcon getStillIcon() {
			return still;
		}

		@Override
		public IIcon getFlowingIcon() {
			return flow;
		}

		@Override
		public int getColor() {
			return ore.colour();
		}

		@Override
		public String getUnlocalizedName() {
			return "fluid." + Reference.MOD_ID + "." + unlocalizedName;
		}

		@Override
		public String getLocalizedName(FluidStack stack) {
			return ore.inGameName();
		}
	}
}