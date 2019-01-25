package com.liqun.www.liqunalifacepay.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.bean.ShoppingBagBean;

import java.util.List;

public class ShoppingBag1Adapter extends RecyclerView.Adapter<ShoppingBag1Adapter.BagItemHolder> {
    private final LayoutInflater mInflater;
    private Context mCtx;
    private List<ShoppingBagBean> mBagList;
    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    public ShoppingBag1Adapter(Context ctx, List<ShoppingBagBean> bagList) {
        mCtx = ctx;
        mBagList = bagList;
        mInflater = LayoutInflater.from(ctx);
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onItemCheckedChangeListener) {
        this.onItemCheckedChangeListener = onItemCheckedChangeListener;
    }

    @NonNull
    @Override
    public BagItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_multiple, viewGroup, false);
        return new BagItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BagItemHolder holder, final int i) {
        ShoppingBagBean bagBean = mBagList.get(i);
        String price = bagBean.getPrice();
        holder.cbType.setChecked(bagBean.isSelected());
        holder.cbType.setText(bagBean.getType());
        holder.tvCoding.setText(bagBean.getProductNo());
        holder.tvPrice.setText(bagBean.getPrice());
        holder.cbType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onItemCheckedChangeListener.onItemCheckedChanged(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBagList != null ? mBagList.size() : 0;
    }

    class BagItemHolder extends RecyclerView.ViewHolder{

        private CheckBox cbType;
        private TextView tvCoding,tvPrice;

        public BagItemHolder(@NonNull View itemView) {
            super(itemView);
            cbType = itemView.findViewById(R.id.cb_type);
            tvCoding = itemView.findViewById(R.id.tv_coding);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }

    /**
     * 列表项选中状态改变
     */
    public interface OnItemCheckedChangeListener{
        void onItemCheckedChanged(int i);
    }
}
