package nurgling.bots;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CollectCropProducts;
import nurgling.bots.actions.PutBestItemBack;
import nurgling.bots.actions.GetBestSeed;
import nurgling.bots.actions.HarvestSeedAction;
import nurgling.bots.actions.HarvestTrellis;
import nurgling.bots.actions.HarvestTrellisRegrow;
import nurgling.bots.actions.SelectFlowerAction;
import nurgling.bots.actions.TransferToTrough;
import nurgling.bots.actions.HarvestAndReplant;
import nurgling.bots.tools.HarvestOut;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class FarmerCarrot extends Bot {

        class CropConfig {
                NAlias seed; // The seed we pick up to replant
                AreasID harvest_area; // Where the crop is planted, must have a container of seeds
                NAlias product; // Additional products, like leaves
                AreasID product_store; // Where to put the product

                public CropConfig(NAlias seed, AreasID harvest_area, NAlias product,
                                AreasID product_store) {
                        this.seed = seed;
                        this.harvest_area = harvest_area;
                        this.product = product;
                        this.product_store = product_store;
                }
        }

        public FarmerCarrot(NGameUI gameUI) {
                super(gameUI);
                win_title = "FarmerCarrot";
                win_sz.y = 100;
                crops = Arrays.asList(
                                new CropConfig(
                                                new NAlias("carrot", "Carrot"),
                                                AreasID.carrot,
                                                null,
                                                null),
                                new CropConfig(new NAlias("beet", "Beet"),
                                                AreasID.beetroot, new NAlias("leaves", "Leaves"),
                                                AreasID.beetrootLeaves),
                                new CropConfig(
                                                new NAlias("flax", "Flax"),
                                                AreasID.flax,
                                                new NAlias("fibre", "Fibre"),
                                                AreasID.flaxFibre),
                                new CropConfig(
                                                new NAlias("hemp", "Hemp"),
                                                AreasID.hemp,
                                                new NAlias("fibre", "Fibre"),
                                                AreasID.hempFibre),
                                new CropConfig(
                                                new NAlias("Seeds", "seed"),
                                                AreasID.lettuce,
                                                new NAlias("Leaf", "leaf"),
                                                AreasID.lettuceLeaf),
                                new CropConfig(new NAlias("Turnip"), AreasID.turnip, null, null),
                                new CropConfig(new NAlias("Onion"), AreasID.yellow_onion, null, null),
                                new CropConfig(new NAlias("Onion"), AreasID.red_onion, null, null),
                                new CropConfig(new NAlias("Seed", "seed"), AreasID.pumpkin, new NAlias("Flesh"),
                                                AreasID.pumpkin_flesh),
                                new CropConfig(new NAlias("seed", "Seed"), AreasID.poppy,
                                                new NAlias("item", "Flower", "Pod"), AreasID.poppyFlower),
                                new CropConfig(new NAlias("seed"), AreasID.pipeweed,
                                                new NAlias("Leaf", "item", "fresh"), AreasID.pipeweedLeaf),
                                new CropConfig(new NAlias("seed"), AreasID.wheat, new NAlias("straw", "Straw"),
                                                AreasID.straw),
                                new CropConfig(new NAlias("seed"), AreasID.barley, new NAlias("straw", "Straw"),
                                                AreasID.straw),
                                new CropConfig(new NAlias("seed"), AreasID.millet, new NAlias("straw", "Straw"),
                                                AreasID.straw),
                                new CropConfig(new NAlias("leek", "Leek"), AreasID.leek, null, null)

                );

                for (CropConfig crop : crops) {
                        runActions.add(new GetBestSeed(crop.seed, crop.harvest_area));
                        runActions.add(new HarvestAndReplant(gameUI, crop.seed,
                                        crop.harvest_area));

                        runActions.add(new PutBestItemBack(crop.seed, crop.harvest_area));
                        runActions.add(new TransferToTrough(crop.seed));

                        if (crop.product != null) {
                                runActions.add(new CollectCropProducts(crop.product, crop.harvest_area));
                                runActions.add(new PutBestItemBack(crop.product, crop.product_store));
                                runActions.add(new TransferToTrough(crop.product));

                        }
                }
        }

        @Override
        public void initAction() {
        }

        @Override
        public void endAction() {
                super.endAction();
        }

        List<CropConfig> crops;
}
