package org.halvors.quantum.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.quantum.client.gui.debug.GuiCreativeBuilder;
import org.halvors.quantum.client.gui.machine.GuiChemicalExtractor;
import org.halvors.quantum.client.gui.machine.GuiGasCentrifuge;
import org.halvors.quantum.client.gui.machine.GuiNuclearBoiler;
import org.halvors.quantum.client.gui.machine.GuiQuantumAssembler;
import org.halvors.quantum.client.gui.particle.GuiAccelerator;
import org.halvors.quantum.client.gui.reactor.fission.GuiReactorCell;
import org.halvors.quantum.client.render.machine.RenderChemicalExtractor;
import org.halvors.quantum.client.render.machine.RenderGasCentrifuge;
import org.halvors.quantum.client.render.machine.RenderNuclearBoiler;
import org.halvors.quantum.client.render.machine.RenderQuantumAssembler;
import org.halvors.quantum.client.render.reactor.RenderElectricTurbine;
import org.halvors.quantum.client.render.reactor.fission.RenderReactorCell;
import org.halvors.quantum.common.CommonProxy;
import org.halvors.quantum.common.QuantumBlocks;
import org.halvors.quantum.common.QuantumItems;
import org.halvors.quantum.common.Reference;
import org.halvors.quantum.common.block.debug.BlockCreativeBuilder;
import org.halvors.quantum.common.block.machine.BlockMachineModel.EnumModelMachine;
import org.halvors.quantum.common.block.reactor.fusion.BlockElectromagnet.EnumElectromagnet;
import org.halvors.quantum.common.tile.machine.TileChemicalExtractor;
import org.halvors.quantum.common.tile.machine.TileGasCentrifuge;
import org.halvors.quantum.common.tile.machine.TileNuclearBoiler;
import org.halvors.quantum.common.tile.machine.TileQuantumAssembler;
import org.halvors.quantum.common.tile.particle.TileAccelerator;
import org.halvors.quantum.common.tile.reactor.TileElectricTurbine;
import org.halvors.quantum.common.tile.reactor.fission.TileReactorCell;
import org.halvors.quantum.common.utility.ResourceUtility;
import org.halvors.quantum.common.utility.type.ResourceType;

/**
 * This is the client proxy used only by the client.
 *
 * @author halvors
 */
@SideOnly(Side.CLIENT)
@EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy implements IGuiHandler {
	@Override
	public void preInit() {
	    // Register our domain to OBJLoader.
		OBJLoader.INSTANCE.addDomain(Reference.DOMAIN);
		//OBJBakedModel.init();

		// Blocks.
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockAccelerator), 0, new ModelResourceLocation(Reference.PREFIX + "accelerator", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockControlRod), 0, new ModelResourceLocation(Reference.PREFIX + "control_rod", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockElectricTurbine), 0, new ModelResourceLocation(Reference.PREFIX + "electric_turbine", "inventory"));

        for (EnumElectromagnet type : EnumElectromagnet.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockElectromagnet), type.ordinal(), new ModelResourceLocation(Reference.PREFIX + "electromagnet", "type=" + type.getName()));
        }

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockFulmination), 0, new ModelResourceLocation(Reference.PREFIX + "fulmination", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockGasFunnel), 0, new ModelResourceLocation(Reference.PREFIX + "gas_funnel", "inventory"));

        for (EnumModelMachine type : EnumModelMachine.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockModelMachine), type.ordinal(), new ModelResourceLocation(Reference.PREFIX + "machine_model", "facing=north,type=" + type.getName()));
        }

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockSiren), 0, new ModelResourceLocation(Reference.PREFIX + "siren", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockThermometer), 0, new ModelResourceLocation(Reference.PREFIX + "thermometer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockUraniumOre), 0, new ModelResourceLocation(Reference.PREFIX + "uranium_ore", "inventory"));
        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockPlasma), 0, new ModelResourceLocation(Reference.PREFIX + "plasma", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockPlasmaHeater), 0, new ModelResourceLocation(Reference.PREFIX + "plasma_heater", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockRadioactiveGrass), 0, new ModelResourceLocation(Reference.PREFIX + "radioactive_grass", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockReactorCell), 0, new ModelResourceLocation(Reference.PREFIX + "reactor_cell", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QuantumBlocks.blockCreativeBuilder), 0, new ModelResourceLocation(Reference.PREFIX + "creative_builder", "inventory"));

        // Item Variants
        ModelBakery.registerItemVariants(QuantumItems.itemAntimatterCell,
                new ResourceLocation(Reference.PREFIX + "cellAntimatter_milligram"),
                new ResourceLocation(Reference.PREFIX + "cellAntimatter_gram")
        );

		// Items.
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemAntimatterCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellAntimatter_milligram", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemAntimatterCell, 1, new ModelResourceLocation(Reference.PREFIX + "cellAntimatter_gram", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemBreederFuel, 0, new ModelResourceLocation(Reference.PREFIX + "breederFuel", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellEmpty", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemDarkMatterCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellDarkMatter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemDeuteriumCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellDeuterium", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemFissileFuel, 0, new ModelResourceLocation(Reference.PREFIX + "fissileFuel", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemTritiumCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellTritium", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemWaterCell, 0, new ModelResourceLocation(Reference.PREFIX + "cellWater", "inventory"));

        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemUranium, 0, new ModelResourceLocation(Reference.PREFIX + "uranium", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemUranium, 1, new ModelResourceLocation(Reference.PREFIX + "uranium", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemYellowCake, 0, new ModelResourceLocation(Reference.PREFIX + "yellowcake", "inventory"));

        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemHazmatMask, 0, new ModelResourceLocation(Reference.PREFIX + "hazmatMask", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemHazmatBody, 0, new ModelResourceLocation(Reference.PREFIX + "hazmatBody", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemHazmatLeggings, 0, new ModelResourceLocation(Reference.PREFIX + "hazmatLeggings", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QuantumItems.itemHazmatBoots, 0, new ModelResourceLocation(Reference.PREFIX + "hazmatBoots", "inventory"));
	}

	@Override
	public void init() {
        // Register special renderer.
		ClientRegistry.bindTileEntitySpecialRenderer(TileChemicalExtractor.class, new RenderChemicalExtractor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileElectricTurbine.class, new RenderElectricTurbine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGasCentrifuge.class, new RenderGasCentrifuge());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNuclearBoiler.class, new RenderNuclearBoiler());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileThermometer.class, new RenderThermometer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileQuantumAssembler.class, new RenderQuantumAssembler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorCell.class, new RenderReactorCell());

        // Register entity renderer.
		//RenderingRegistry.registerEntityRenderingHandler(EntityParticle.class, new RenderParticle());
	}

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
	    final TextureMap textureMap = event.getMap();

	    for (EnumModelMachine type : EnumModelMachine.values()) {
            textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, type.getName()));
        }

        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "electric_turbine_large"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "electric_turbine_small"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "reactor_cell_bottom"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "reactor_cell_middle"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "reactor_cell_top"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "reactor_cell_top"));
        textureMap.registerSprite(ResourceUtility.getResource(ResourceType.TEXTURE_MODELS, "reactor_fissile_material"));
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();

		if (block instanceof BlockCreativeBuilder) {
			return new GuiCreativeBuilder(pos);
		}

		if (tile instanceof TileAccelerator) {
			return new GuiAccelerator(player.inventory, (TileAccelerator) tile);
		} else if (tile instanceof TileGasCentrifuge) {
			return new GuiGasCentrifuge(player.inventory, (TileGasCentrifuge) tile);
		} else if (tile instanceof TileChemicalExtractor) {
			return new GuiChemicalExtractor(player.inventory, (TileChemicalExtractor) tile);
		} else if (tile instanceof TileNuclearBoiler) {
			return new GuiNuclearBoiler(player.inventory, (TileNuclearBoiler) tile);
		} else if (tile instanceof TileQuantumAssembler) {
			return new GuiQuantumAssembler(player.inventory, (TileQuantumAssembler) tile);
		} else if (tile instanceof TileReactorCell) {
			return new GuiReactorCell(player.inventory, (TileReactorCell) tile);
		}

		return null;
	}

	@Override
	public void registerItemRenderer(Item item, int metadata, String id) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(Reference.PREFIX + id, "inventory"));
	}

	@Override
	public EntityPlayer getPlayer(MessageContext context) {
		if (context.side.isServer()) {
			return context.getServerHandler().playerEntity;
		} else {
			return Minecraft.getMinecraft().player;
		}
	}
}