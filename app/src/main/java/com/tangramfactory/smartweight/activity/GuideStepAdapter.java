package com.tangramfactory.smartweight.activity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.vo.GuideStepVo;

import java.util.List;

/**
 * Created by B on 2016-05-24.
 */
public class GuideStepAdapter extends RecyclerView.Adapter<GuideStepAdapter.ListItemViewHolder> {
    private List<GuideStepVo> items;
    private static GuideStepAdapter adapter;

    GuideStepAdapter(List<GuideStepVo> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        adapter = this;
    }

    public void addData(GuideStepVo newModelData, int position) {
        items.add(position, newModelData);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public GuideStepVo getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guide_step_list_item, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        GuideStepVo model = items.get(position);

        if(model.isCurrentStep()) {
            viewHolder.rootLayout.setBackgroundColor(Color.parseColor("#4BC1F2"));
        }
        int stepCount = Integer.parseInt(model.getStepCount());
        viewHolder.stepCountText.setText(String.format("%02d", stepCount));
        int badgeCount = Integer.parseInt(model.getBageCount());
        for(int i=0 ; i < badgeCount; i++) {
            viewHolder.badge[i].setBackgroundResource(R.drawable.badge);
        }
        viewHolder.repsCount.setText(model.getRepsCount());
        if(position == getItemCount()-1) {
            viewHolder.bottomLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout;
        TextView stepCountText;
        ImageButton[] badge = new ImageButton[3];
        TextView repsCount;
        LinearLayout bottomLine;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            rootLayout = (LinearLayout)itemView.findViewById(R.id.rootLayout);
            stepCountText = (TextView) itemView.findViewById(R.id.step_count);
            badge[0] = (ImageButton) itemView.findViewById(R.id.badge1);
            badge[1] = (ImageButton) itemView.findViewById(R.id.badge2);
            badge[2] = (ImageButton) itemView.findViewById(R.id.badge3);
            repsCount = (TextView) itemView.findViewById(R.id.repsCount);
            bottomLine = (LinearLayout)itemView.findViewById(R.id.bottomLine);
        }
    }
}
