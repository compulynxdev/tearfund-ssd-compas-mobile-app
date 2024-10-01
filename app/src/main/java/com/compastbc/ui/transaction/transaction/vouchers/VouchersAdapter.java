package com.compastbc.ui.transaction.transaction.vouchers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.compastbc.R;
import com.compastbc.core.data.db.model.Vouchers;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class VouchersAdapter extends RecyclerView.Adapter {
    private final List<Vouchers> vouchersList;
    private ClickListener listener;

    VouchersAdapter(List<Vouchers> list) {
        this.vouchersList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_programmes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        VouchersAdapter.ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.voucherTitle.setText(vouchersList.get(position).getVoucherName());
        viewHolder.cardView.setTag(R.id.holder_id, vouchersList.get(position).getVoucherId());

        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(position);

        if (vouchersList.get(position).getVoucherName().length() > 3) {
            String lettersForName = vouchersList.get(position).getVoucherName().substring(0, 2);
            TextDrawable letterDrawable = TextDrawable.builder()
                    .buildRound(lettersForName, color);
            viewHolder.imageView.setImageDrawable(letterDrawable);
        } else {
            TextDrawable letterDrawable = TextDrawable.builder()
                    .buildRound(vouchersList.get(position).getVoucherName(), color);
            viewHolder.imageView.setImageDrawable(letterDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return vouchersList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }

    public interface ClickListener {
        void onClick(View v, int voucherId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView voucherTitle;
        private final CardView cardView;
        private final ImageView imageView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            voucherTitle = itemView.findViewById(R.id.program_title);
            imageView = itemView.findViewById(R.id.program_image);
            cardView = itemView.findViewById(R.id.cardview);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cardview) {
                if (listener != null) {
                    listener.onClick(v, Integer.parseInt(v.getTag(R.id.holder_id).toString()));
                }
            }
        }
    }
}
