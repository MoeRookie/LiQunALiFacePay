package com.liqun.www.liqunalifacepay.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean;
import com.liqun.www.liqunalifacepay.ui.activity.SelfHelpPayActivity;

import java.util.List;

import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.*;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {
    private Context mCtx;
    private List<ScanGoodsResponseBean> mList;
    private LayoutInflater mInflater;

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    private OnItemDeleteClickListener onItemDeleteClickListener;

    public GoodsAdapter(Context ctx, List<ScanGoodsResponseBean> list) {
        mCtx = ctx;
        mList = list;
        mInflater = LayoutInflater.from(mCtx);
    }

    @NonNull
    @Override
    public GoodsAdapter.GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_goods, viewGroup, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsAdapter.GoodsViewHolder holder, final int i) {
        ScanGoodsResponseBean goodsBean = mList.get(i);
        holder.mTvName.setText(goodsBean.getFname()); // 商品名称
        holder.mTvPrice1.setText("￥ " + goodsBean.getPrice()); // 实际售价
        holder.mTvCount.setText((int) goodsBean.getQty()+goodsBean.getUnits()); // 数量+单位
        holder.mTvPrice2.setText("￥ " + goodsBean.getPrice()); // 实际售价
        holder.mIbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDeleteClickListener.onItemDeleteClicked(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private TextView mTvPrice1;
        private TextView mTvCount;
        private TextView mTvPrice2;
        private TextView mIbDelete;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvPrice1 = itemView.findViewById(R.id.tv_price_1);
            mTvCount = itemView.findViewById(R.id.tv_count);
            mTvPrice2 = itemView.findViewById(R.id.tv_price_2);
            mIbDelete = itemView.findViewById(R.id.ib_delete);
        }
    }
    public interface OnItemDeleteClickListener{
        void onItemDeleteClicked(int i);
    }
}
