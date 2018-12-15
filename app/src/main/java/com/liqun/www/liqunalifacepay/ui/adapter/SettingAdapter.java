package com.liqun.www.liqunalifacepay.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter
        <SettingAdapter.SettingItemViewHolder> {
    private Context mCtx;
    private List<SettingItemBean> mItemList;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SettingAdapter(Context ctx, List<SettingItemBean> itemList) {
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
    public void onBindViewHolder(@NonNull final SettingItemViewHolder holder, final int i) {
        SettingItemBean item = mItemList.get(i);
        holder.mTvTitle.setText(item.getTitleId());
        holder.mTvContent.setText(item.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(i);
            }
        });
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
