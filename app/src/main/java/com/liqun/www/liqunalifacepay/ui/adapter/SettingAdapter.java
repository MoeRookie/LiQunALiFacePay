package com.liqun.www.liqunalifacepay.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.bean.SettingItem;

import java.util.List;
import java.util.zip.Inflater;

public class SettingAdapter extends RecyclerView.Adapter
        <SettingAdapter.SettingItemViewHolder> {
    private Context mCtx;
    private List<SettingItem> mItemList;
    private LayoutInflater mInflater;
    public SettingAdapter(Context ctx, List<SettingItem> itemList) {
        mCtx = ctx;
        mItemList = itemList;
        mInflater = LayoutInflater.from(mCtx);
    }

    @NonNull
    @Override
    public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_setting,viewGroup,false);
        return new SettingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingItemViewHolder holder, int i) {
        SettingItem item = mItemList.get(i);
        holder.mTvTitle.setText(item.getTitleId());
        holder.mTvContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return mItemList != null ? mItemList.size() : 0;
    }
    class SettingItemViewHolder extends RecyclerView.ViewHolder{

        private TextView mTvTitle;
        private TextView mTvContent;

        public SettingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
