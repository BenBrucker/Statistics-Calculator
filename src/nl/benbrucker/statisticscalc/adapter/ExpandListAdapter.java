package nl.benbrucker.statisticscalc.adapter;

import java.util.ArrayList;

import nl.benbrucker.statisticscalc.Calculator;
import nl.benbrucker.statisticscalc.DrawCurve;
import nl.benbrucker.statisticscalc.ExpandListChild;
import nl.benbrucker.statisticscalc.ExpandListGroup;
import nl.benbrucker.statisticscalc.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ExpandListAdapter extends BaseExpandableListAdapter{

	/*
	 * 
	 */
	private int lastExpandedGroupPosition = -1;
	private Context context;
	private ArrayList<ExpandListGroup> groups;
	private ExpandableListView expandableListView;
	private Calculator calc;
	private DrawCurve curve;
	
	public ExpandListAdapter(Context context, ArrayList<ExpandListGroup> groups, ExpandableListView expandableListView, Calculator calc, DrawCurve curve) {
		this.curve = curve;
		this.context = context;
		this.groups = groups;
		this.expandableListView = expandableListView;
		this.calc = calc;
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
		return chList.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		final ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
		LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = infalInflater.inflate(R.layout.expandlist_child, null);
		final SeekBar bar = (SeekBar) view.findViewById(R.id.seekBar1);
		final TextView text = (TextView) view.findViewById(R.id.info_text1);
		
		Calculator.FUNCTION_TYPE tempfType = null;
		switch	(groupPosition)	{
			case 0: tempfType = Calculator.FUNCTION_TYPE.percentielen; break;
			case 1: tempfType = Calculator.FUNCTION_TYPE.decielen; break;
			case 2: tempfType = Calculator.FUNCTION_TYPE.zScores; break;
			case 3: tempfType = Calculator.FUNCTION_TYPE.tScores; break;
			case 4: tempfType = Calculator.FUNCTION_TYPE.cScores; break;
			default: tempfType = Calculator.FUNCTION_TYPE.decielen; break;
		}
		final Calculator.FUNCTION_TYPE fType = tempfType;
		bar.setProgress((int) calc.getValue(fType).getValueOne() - child.getMinValue());
		text.setText(calc.getValue(fType).toString());
		bar.setMax(child.getMaxValue() - child.getMinValue());
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					//text.setText(Integer.toString(child.getMinValue() + seekBar.getProgress()));
					calc.setNewValue(child.getMinValue() + seekBar.getProgress(), fType);
					text.setText(calc.getValue(fType).toString());
					curve.update();
				}
				public void onStartTrackingTouch(SeekBar seekBar) {
					curve.update();
				}
	
				public void onStopTrackingTouch(SeekBar seekBar) {
					Log.d("Class: ExpandAdapter", "On Stop Tracking");
					Log.d("fType", fType.toString());
					Log.d("child.minValue", Integer.toString(child.getMinValue()));
					Log.d("seekBar.getProgress", Integer.toString(seekBar.getProgress()));
					calc.setNewValue(child.getMinValue() + seekBar.getProgress(), fType);
					text.setText(calc.getValue(fType).toString());
					curve.update();
				}
				
			}
		);
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
		return chList.size();

	}

	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ExpandListGroup group = (ExpandListGroup) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inf.inflate(R.layout.expandlist_group, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.groupName);
		TextView infoText = (TextView) convertView.findViewById(R.id.groupValue);;
		tv.setText(group.getName());
		Calculator.FUNCTION_TYPE fType = null;
		switch	(groupPosition)	{
			case 0: fType = Calculator.FUNCTION_TYPE.percentielen; break;
			case 1: fType = Calculator.FUNCTION_TYPE.decielen; break;
			case 2: fType = Calculator.FUNCTION_TYPE.zScores; break;
			case 3: fType = Calculator.FUNCTION_TYPE.tScores; break;
			case 4: fType = Calculator.FUNCTION_TYPE.cScores; break;
			default: fType = Calculator.FUNCTION_TYPE.decielen; break;
		}
		infoText.setText(calc.getValue(fType).toString());
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
	
	@Override
    public void onGroupExpanded(int groupPosition){
        //collapse the old expanded group, if not the same
        //as new group to expand
        if(groupPosition != lastExpandedGroupPosition){
        	expandableListView.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);           
        lastExpandedGroupPosition = groupPosition;
    }

}


