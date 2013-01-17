package com.viisi.droid.missedcallnotification.expandablelist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.viisi.droid.missedcallnotification.R;

public class ExpandableAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<NotificationDelayGroup> groups;

	public ExpandableAdapter(Context context, ArrayList<NotificationDelayGroup> groups) {
		this.context = context;
		this.groups = groups;
	}

	public void addItem(NotificationDelayOptions item, NotificationDelayGroup group) {
		if (!groups.contains(group)) {
			groups.add(group);
		}
		int index = groups.indexOf(group);
		ArrayList<NotificationDelayOptions> ch = groups.get(index).getOpcoes();
		ch.add(item);
		groups.get(index).setOpcoes(ch);
	}

	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<NotificationDelayOptions> chList = groups.get(groupPosition).getOpcoes();
		return chList.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		NotificationDelayOptions child = (NotificationDelayOptions) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.notificationTextChild);
		tv.setText(child.getNome().toString());
		tv.setTag(child.getTag());
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		ArrayList<NotificationDelayOptions> chList = groups.get(groupPosition).getOpcoes();

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

	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		NotificationDelayGroup group = (NotificationDelayGroup) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.notificationTextGroup);
		tv.setText(group.getNome());
		return view;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

}
