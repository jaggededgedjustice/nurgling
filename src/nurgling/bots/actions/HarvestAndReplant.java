package nurgling.bots.actions;

import java.util.Arrays;

import haven.Coord2d;
import haven.GItem;
import haven.GameUI;
import haven.Gob;
import haven.MCache;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NGob;
import nurgling.NInventory;
import nurgling.NGItem;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

public class HarvestAndReplant implements Action {
    NAlias crop;
    NAlias seed;
    AreasID harvest_area;
    NGameUI gameUI;

    public HarvestAndReplant(NGameUI gameUI, NAlias seed, AreasID harvest_area) {
        this.gameUI = gameUI;
        this.seed = seed;
        this.harvest_area = harvest_area;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        NArea input = Finder.findNearestMark(harvest_area);
        Thread.sleep(100);
        Gob plant = Finder.findCropInArea(30000, input, true);


        if (plant == null) {
            System.out.println("no crop found");
            return new Results(Results.Types.SUCCESS);
        }

        Coord2d location = plant.rc;

        new PathFinder(gui, plant).run();
        int size = gui.getInventory().getFreeSpace();
        new SelectFlowerAction(plant, "Harvest", SelectFlowerAction.Types.Gob).run(gui);
        NUtils.waitEvent(() -> NUtils.getProg() < 0 && size != gui.getInventory().getFreeSpace(), 60);

        if (Finder.findObjectInArea(new NAlias("lettuce"), 300000, input) != null) {
            new CollectCropProducts(new NAlias("lettucehead", "item"), harvest_area).run(gui);
            NUtils.waitEvent(() -> gui.getInventory().getItem(new NAlias("lettucehead")) != null, 500);
            NGItem lettuce = (NGItem) gui.getInventory().getItem(new NAlias("lettucehead"));
            new SelectFlowerAction(lettuce, "Split", SelectFlowerAction.Types.Item).run(gui);
            NUtils.waitEvent(() -> gui.getInventory().getItem(new NAlias("Leaf")) != null, 500);
        } else if (Finder.findObjectInArea(new NAlias("pumpkin"), 30000, input) != null) {
            System.out.println("finding pumpkin");
            for(Gob gob: Finder.findObjectsInArea(input)) {
                System.out.println(gob.getres());
            }
            new CollectCropProducts(new NAlias("pumpkin", "Pumpkin"), harvest_area);
            NAlias alias = new NAlias(Arrays.asList("Pumpkin"), Arrays.asList("seed")); 
            NUtils.waitEvent(() -> gui.getInventory().getItem(alias) != null, 500);
            NGItem pumpkin = (NGItem) gui.getInventory().getItem(alias);
            new SelectFlowerAction(pumpkin, "Slice", SelectFlowerAction.Types.Item).run(gui);
            NUtils.waitEvent(() -> gui.getInventory().getItem(new NAlias("Flesh")) != null, 500);
        }

        NUtils.takeItemToHand(gui.getInventory().getItem(seed, NInventory.QualityType.High).item);
        NUtils.item_action(location, 1);
        Thread.sleep(200);
        NUtils.waitEvent(() -> !gameUI.hand.isEmpty(), 20);
        Thread.sleep(200);
        NUtils.transferToInventory();

        return new Results(Results.Types.SUCCESS);
    }
}
