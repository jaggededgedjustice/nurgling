package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;

import nurgling.*;
import nurgling.NInventory.QualityType;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;



public class PutBestItemBack implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {

        NArea store_area = Finder.findNearestMark(work_area);
        if (store_area == null) {
            System.out.println("Failed to find area " + work_area.toString());
            return new Results(Results.Types.NO_PLACE);
        }

        Gob box = Finder.findObjectInArea(NUtils.CONTAINERS, 100000.0, store_area);
        String cap = NUtils.getContainerType(box).cap;
        new PathFinder(gui, box).run();
        if (new OpenTargetContainer(box, cap).run(gui).type != Results.Types.SUCCESS) {
            return new Results(Results.Types.OPEN_FAIL);
        }

        NInventory inv = gui.getInventory();
        NInventory store = gui.getInventory(cap);
        int needed_space = inv.getGItems(items).size();
        if (needed_space == 0) {
            return new Results(Results.Types.SUCCESS);
        }
        GItem example = inv.getGItems(items).get(0);

        // TODO - check the quality of our items against what's in the container to skip
        // this bit if posible
        // We need enough free space to be able to put all our current items (if they
        // are better)
        while (store.getNumberFreeCoord(example) < needed_space) {
            NUtils.transferItem(store, store.getItem(items, QualityType.Low).item, inv);
        }

        // now put back the best items in our inventory until it is full. The items that
        // remain are the lowest quality available
        while (store.getNumberFreeCoord(example) > 0 && inv.getGItems(items).size() > 0) {
            NUtils.transferItem(inv, inv.getItem(items, QualityType.High).item, store);
        }


        return new Results(Results.Types.SUCCESS);
    }

    public PutBestItemBack(
            NAlias items,
            AreasID work_area) {
        this.items = items;
        this.work_area = work_area;
    }

    NAlias items;
    AreasID work_area;
}
