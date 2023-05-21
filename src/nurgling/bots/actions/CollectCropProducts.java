package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class CollectCropProducts implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        NArea input = Finder.findNearestMark(harvest_area);

        ArrayList<Gob> candidates = Finder.findObjectsInArea(items, input);
        if (candidates.isEmpty()) {
            System.out.println("found no products");
            return new Results(Results.Types.SUCCESS);
        }
        for (Gob gob : candidates) {
            new PathFinder(gui, gob).run();
            Thread.sleep(100);
            NUtils.takeFromEarth(gob);
        }
        return new Results(Results.Types.SUCCESS);
    }

    public CollectCropProducts(
            NAlias items,
            AreasID harvest_area) {
        this.items = items;
        this.harvest_area = harvest_area;
    }

    NAlias items;
    AreasID harvest_area;
}
