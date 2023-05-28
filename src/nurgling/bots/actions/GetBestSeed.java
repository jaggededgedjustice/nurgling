package nurgling.bots.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import haven.GItem;
import haven.Gob;
import haven.ItemInfo;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NInventory;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.NInventory.QualityType;
import nurgling.NProperties.Container;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.NGItem;

public class GetBestSeed implements Action {

    NAlias crop;
    AreasID harvest_area;

    public GetBestSeed(NAlias crop, AreasID harvest_area) {
        this.crop = crop;
        this.harvest_area = harvest_area;
    }

    Comparator<GItem> quality = new Comparator<GItem>() {
        @Override
        public int compare(
                GItem o1,
                GItem o2) {
            // Sort quality high -> low
            return Double.compare(((NGItem) o2).quality(), ((NGItem) o1).quality());
        }
    };

    static int getAmount(GItem item) {
        int ret = 1000;
        synchronized (item.ui) {
            for (ItemInfo o : item.info()) {
                if (o instanceof GItem.Amount)
                    ret = ((GItem.Amount) o).itemnum();
            }
        }
        return ret;
    }

    void compact_seed(NInventory store) throws InterruptedException {
        // find all the stacks of seeds that aren't full organised by quality
        HashMap<Double, ArrayList<GItem>> quantity = new HashMap<Double, ArrayList<GItem>>();
        for (GItem item : store.getWItems()) {
            Double q = ((NGItem) item).quality();
            if (!quantity.containsKey(q)) {
                quantity.put(q, new ArrayList<GItem>());
            }
            int count = getAmount(item);
            if (count < 50) {
                quantity.get(q).add(item);
            }
        }

        for (Double q : quantity.keySet()) {
            
            ArrayList<GItem> list = quantity.get(q);
            if (list.size() < 2) {
                continue;
            }
            // now look through the list of stacks to see if any can be put together

            for (GItem first : list) {
                for (GItem second : list) {
                    if (first == second) {
                        continue;
                    }
                    if ((getAmount(first) + getAmount(second)) <= 50) {
                        NUtils.takeItemToHand(first);
                        Thread.sleep(200);
                        second.wdgmsg("itemact", 3); // click the 2 piles together
                        Thread.sleep(200);
                        NUtils.transferToInventory();

                        //compact the remainder
                        compact_seed(store);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {

        Gob box = Finder.findObjectInArea(NUtils.CONTAINERS, 100000.0, Finder.findNearestMark(harvest_area));
        String cap = NUtils.getContainerType(box).cap;

        new PathFinder(gui, box).run();
        if (new OpenTargetContainer(box, cap).run(gui).type != Results.Types.SUCCESS) {
            return new Results(Results.Types.OPEN_FAIL);
        }

        Thread.sleep(400);

        NInventory store = gui.getInventory(cap);
        compact_seed(store);

        Vector<GItem> seeds = new Vector<GItem>();
        for (GItem item : store.getGItems(crop)) {
            if (getAmount(item) >= 5) {
                seeds.addElement(item);
            }
        }
        if (seeds.size() == 0) {
            System.out.println("Found no seeds");
            return new Results(Results.Types.SUCCESS);
        }

        seeds.sort(quality);

        NUtils.transferItem(store, seeds.firstElement(), gui.getInventory());

        return new Results(Results.Types.SUCCESS);

    }
}
