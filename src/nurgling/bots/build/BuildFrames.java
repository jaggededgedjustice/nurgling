package nurgling.bots.build;

import haven.Button;
import haven.Coord;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.Bot;
import nurgling.bots.actions.Build;
import nurgling.bots.tools.CraftCommand;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class BuildFrames extends Bot {
    
    
    public BuildFrames ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BuildFrame";
        win_sz.y = 150;
        
        CraftCommand command = new CraftCommand ();
        command.command = new char[]{ 'b', 'b', 'd' };
        command.name = "Drying Frame";
        command.ingredients = new ArrayList<Ingredient> ();
        Ingredient bough = new Ingredient();
        bough.item = new NAlias( "bough" );
        bough.isGroup = true;
        command.ingredients.add (bough);
        command.spec_in_area.put(bough,bough_area);
        command.ing_count.put(bough,2);
        Ingredient string = new Ingredient();
        string.item = new NAlias ( new ArrayList<String> ( Arrays.asList ( "fibre", "taproot","nettle","toughroot", "hidestrap" ) ));
        string.isGroup = true;
        command.ingredients.add (string);
        command.spec_in_area.put(string,fibre_area);
        command.ing_count.put(string,2);
        Ingredient branch = new Ingredient();
        branch.item = new NAlias ( "branch");
        branch.isGroup = true;
        command.ingredients.add (branch);
        command.spec_in_area.put(branch,branch_area);
        command.ing_count.put(branch,5);

        ///Добавление цикла в действия бота
        runActions.add ( new Build( build_area, "gfx/terobjs/dframe", command ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Building area" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, build_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Boughs" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, bough_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Strings" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, fibre_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Branches" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone3, m_selection_start, branch_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        
        while ( !_start.get () || !_zone1.get () || !_zone2.get () || !_zone3.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _zone1.set ( false );
        _zone2.set ( false );
        _zone3.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private AtomicBoolean _zone3 = new AtomicBoolean ( false );
    private NArea build_area = new NArea ();
    private NArea bough_area = new NArea ();
    private NArea fibre_area = new NArea ();
    private NArea branch_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
