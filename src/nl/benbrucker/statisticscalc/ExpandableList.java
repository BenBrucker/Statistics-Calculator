package nl.benbrucker.statisticscalc;

import java.util.ArrayList;
import java.util.Observable;
import nl.benbrucker.statisticscalc.adapter.ExpandListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

public class ExpandableList extends Activity {
    /** Called when the activity is first created. */
	private ExpandListAdapter ExpAdapter;
	private ArrayList<ExpandListGroup> ExpListItems;
	private ExpandableListView ExpandList;
	private static Calculator calc;
	private DrawCurve curve;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        calc = new Calculator();
        DrawCurve curve = new DrawCurve(this);
        ExpandList = (ExpandableListView) findViewById(R.id.ExpList);
        ExpListItems = SetStandardGroups();
        ExpAdapter = new ExpandListAdapter(ExpandableList.this, ExpListItems, ExpandList, calc, curve);
        ExpandList.setAdapter(ExpAdapter);
        final Button button = (Button) findViewById(R.id.Confirm_Button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	for (int i = 0; i < ExpandList.getCount(); i++)
            	ExpandList.collapseGroup(i);
            }
        });
    }
    
    public ArrayList<ExpandListGroup> SetStandardGroups() {
    	ArrayList<ExpandListGroup> groups = new ArrayList<ExpandListGroup>();
    	ArrayList<ExpandListChild> childs = new ArrayList<ExpandListChild>();
        
    	ExpandListGroup percentielen = new ExpandListGroup();
    	percentielen.setName("Percentielen");
        ExpandListChild percentielenChild = new ExpandListChild();
        percentielenChild.setMinValue(1);
        percentielenChild.setMaxValue(99);
        childs.add(percentielenChild);        
        percentielen.setItems(childs);
        groups.add(percentielen);

        childs = new ArrayList<ExpandListChild>();
    	
    	ExpandListGroup decielen = new ExpandListGroup();
        decielen.setName("Decielen");
        ExpandListChild decielenChild = new ExpandListChild();
        decielenChild.setMinValue(1);
        decielenChild.setMaxValue(10);
        childs.add(decielenChild);        
        decielen.setItems(childs);
        groups.add(decielen);
        
        childs = new ArrayList<ExpandListChild>();
        
        
    	ExpandListGroup zscores = new ExpandListGroup();
    	zscores.setName("Z-Scores");
        ExpandListChild zscoresChild = new ExpandListChild();
        zscoresChild.setMinValue(-4);
        zscoresChild.setMaxValue(4);
        childs.add(zscoresChild);        
        zscores.setItems(childs);
        groups.add(zscores);

        childs = new ArrayList<ExpandListChild>();
        
    	ExpandListGroup tscores = new ExpandListGroup();
    	tscores.setName("T-Scores");
        ExpandListChild tscoresChild = new ExpandListChild();
        tscoresChild.setMinValue(10);
        tscoresChild.setMaxValue(90);
        childs.add(tscoresChild);        
        tscores.setItems(childs);
        groups.add(tscores);

        childs = new ArrayList<ExpandListChild>();
        
    	ExpandListGroup cscores = new ExpandListGroup();
    	cscores.setName("C-Scores");
        ExpandListChild cscoresChild = new ExpandListChild();
        cscoresChild.setMinValue(0);
        cscoresChild.setMaxValue(10);
        childs.add(cscoresChild);        
        cscores.setItems(childs);
        groups.add(cscores);
        
        return groups;
    }

	public void update(Observable observable, Object data) {
		ExpAdapter = new ExpandListAdapter(ExpandableList.this, ExpListItems, ExpandList, calc, curve);
        ExpandList.setAdapter(ExpAdapter);
	}

}
