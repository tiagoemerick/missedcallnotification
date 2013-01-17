package com.viisi.droid.missedcallnotification.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;

public class MissedCallListAdapter extends ArrayAdapter<MissedCallLog> {

	Context context;
	int layoutResourceId;
	List<MissedCallLog> data = null;

	public MissedCallListAdapter(Context context, int layoutResourceId, List<MissedCallLog> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MissedCallLogHolder mclHolder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			mclHolder = new MissedCallLogHolder();
			mclHolder.list_image_not_type = (ImageView) row.findViewById(R.id.list_image_not_type);
			mclHolder.missedNumberText = (TextView) row.findViewById(R.id.missedNumberText);
			mclHolder.timeMissedCallText = (TextView) row.findViewById(R.id.timeMissedCallText);
			mclHolder.timeToFireEvent = (TextView) row.findViewById(R.id.timeToFireEvent);

			row.setTag(mclHolder);
		} else {
			mclHolder = (MissedCallLogHolder) row.getTag();
		}

		MissedCallLog mcl = data.get(position);
		mclHolder.missedNumberText.setText(mcl.getMissedNumber() + mcl.getContactName());
		mclHolder.list_image_not_type.setImageResource(mcl.getImageType());
		mclHolder.timeMissedCallText.setText(mcl.getLogTimeFormated());
		mclHolder.timeToFireEvent.setText(mcl.getScheduleTimeLeftMinutes());

		return row;
	}

	static class MissedCallLogHolder {
		ImageView list_image_not_type;
		TextView missedNumberText;
		TextView timeMissedCallText;
		TextView timeToFireEvent;
	}
}
