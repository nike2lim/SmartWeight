package com.tangramfactory.smartweight.activity.workoutresult;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.vo.GuideResultVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shlim on 2016-05-28.
 */
public class WorkoutSetResultAdapter extends HeaderRecyclerViewAdapter implements View.OnClickListener{
    private List<GuideResultVo> items;
    private static WorkoutSetResultAdapter adapter;
    private Context mContext;

    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    private static final int TYPE_ADAPTEE_OFFSET = 2;

    WorkoutSetResultAdapter(List<GuideResultVo> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        adapter = this;
    }

    public void addData(GuideResultVo newModelData, int position) {
        items.add(position, newModelData);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public GuideResultVo getItem(int position) {
        if(items.size() <= position) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public boolean useHeader() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean useFooter() {
        return true;
    }

    @Override
    public FooterViewHolder onCreateFooterViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_item_footer, parent, false);
        return new FooterViewHolder(itemView);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
        FooterViewHolder footerViewHolder = (FooterViewHolder)holder;

        footerViewHolder.shareInnstagramButton.setOnClickListener(this);
        footerViewHolder.shareFacebookButton.setOnClickListener(this);
        footerViewHolder.shareTwitterButton.setOnClickListener(this);
        footerViewHolder.shareGooglePlusButton.setOnClickListener(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_item, parent, false);

        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        GuideResultVo model = items.get(position);
        ArrayList<GuideResultVo.SetInfo> infoList = model.getSetInfoList();

        ListItemViewHolder listItemViewHolder = (ListItemViewHolder)holder;
        listItemViewHolder.exerciseType.setText(model.getExerciseName());
        listItemViewHolder.progressText.setText(String.valueOf(model.getProgress()));
        listItemViewHolder.progressBar.setProgress(model.getProgress());
        listItemViewHolder.restTime.setText(model.getRestTime());
        listItemViewHolder.totalTime.setText(model.getTotalTime());

        if(null != infoList) {
            for(int i=0; i < infoList.size(); i++) {
                GuideResultVo.SetInfo info = infoList.get(i);
                listItemViewHolder.weightUnit.setText(info.getWeightUnit());
                listItemViewHolder.setCountText[i].setText(info.getSetNum() + " set");
                listItemViewHolder.weightText[i].setText(info.getWeight());
                listItemViewHolder.repsPerTotalText[i].setText(info.getResultReps() + "/" + info.getTotalReps());
                listItemViewHolder.accuracyText[i].setText(info.getAccuracy());
            }

            if(infoList.size() == 1) {
                listItemViewHolder.itemView.findViewById(R.id.set2).setVisibility(View.GONE);
                listItemViewHolder.itemView.findViewById(R.id.set3).setVisibility(View.GONE);
            }else if(infoList.size() == 2)  {
                listItemViewHolder.itemView.findViewById(R.id.set3).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getBasicItemCount() {
        return items.size();
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_instagram:
                Toast.makeText(v.getContext(), "Share Instagram!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_facebook:
                Toast.makeText(v.getContext(), "Share FaceBook!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_twitter:
                Toast.makeText(v.getContext(), "Share Twitter!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_google_plus:
                Toast.makeText(v.getContext(), "Share Google Plus!", Toast.LENGTH_SHORT).show();
                break;
        }

    }


//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        GuideResultVo model = items.get(position);
//        ArrayList<GuideResultVo.SetInfo> infoList = model.getSetInfoList();
//
//        ListItemViewHolder listItemViewHolder = (ListItemViewHolder)viewHolder;
//        listItemViewHolder.exerciseType.setText(model.getExerciseType());
//        listItemViewHolder.progressText.setText(String.valueOf(model.getProgress()));
//        listItemViewHolder.progressBar.setProgress(model.getProgress());
//
//        if(null != infoList) {
////            for(GuideResultVo.SetInfo info : infoList) {
//            for(int i=0; i < 3; i++) {
//                GuideResultVo.SetInfo info = infoList.get(i);
//                listItemViewHolder.setCountText[i].setText(info.getSetNum());
//                listItemViewHolder.weightText[i].setText(info.getWeight() + info.getWeightUnit());
//                listItemViewHolder.repsPerTotalText[i].setText(info.getResultReps() + "/" + info.getTotalReps());
//                listItemViewHolder.accuracyText[i].setText(info.getAccuracy());
//
//            }
//        }
//    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseType;
        TextView progressText;
        TextView weightUnit;
        TextView restTime;
        TextView totalTime;
        TextView[] setCountText = new TextView[3];
        TextView[] weightText = new TextView[3];
        TextView[] repsPerTotalText  = new TextView[3];
        TextView[] accuracyText  = new TextView[3];
        ProgressBar progressBar;


        public ListItemViewHolder(View itemView) {
            super(itemView);

            exerciseType = (TextView)itemView.findViewById(R.id.exercise_type);
            progressText = (TextView)itemView.findViewById(R.id.text_progress);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            weightUnit = (TextView)itemView.findViewById(R.id.weightunit);
            restTime = (TextView)itemView.findViewById(R.id.rest_time);
            totalTime = (TextView)itemView.findViewById(R.id.total_time);

            Context context = itemView.getContext();

            for(int i=0; i < 3; i++) {

                int resId = context.getResources().getIdentifier("set"+(i+1), "id", context.getPackageName());
                LinearLayout setLayout = (LinearLayout)itemView.findViewById(resId);

                setCountText[i] = (TextView)setLayout.findViewById(R.id.set_count);
                weightText[i] = (TextView)setLayout.findViewById(R.id.weight);
                repsPerTotalText[i] = (TextView)setLayout.findViewById(R.id.reps_per_total);
                accuracyText[i] = (TextView)setLayout.findViewById(R.id.accuracy);
            }
        }
    }


    public final static class FooterViewHolder extends RecyclerView.ViewHolder {

        ImageButton shareInnstagramButton;
        ImageButton shareFacebookButton;
        ImageButton shareTwitterButton;
        ImageButton shareGooglePlusButton;


        public FooterViewHolder(View itemView) {
            super(itemView);

            shareInnstagramButton = (ImageButton)itemView.findViewById(R.id.share_instagram);
            shareFacebookButton = (ImageButton)itemView.findViewById(R.id.share_facebook);
            shareTwitterButton = (ImageButton)itemView.findViewById(R.id.share_twitter);
            shareGooglePlusButton = (ImageButton)itemView.findViewById(R.id.share_google_plus);
        }
    }

}
