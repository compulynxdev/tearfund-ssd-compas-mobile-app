package com.compastbc.ui.transaction.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.compastbc.R;
import com.compastbc.core.data.db.model.Programs;
import java.util.List;

public class TransactionProgramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Programs> list;
    private ItemClickListener clickListener;

    TransactionProgramAdapter(List<Programs> programmesList) {
        this.list = programmesList;
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
        TransactionProgramAdapter.ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.programTitle.setText(list.get(position).getProgramName());
        viewHolder.cardView.setTag(R.id.holder_id, list.get(position).getProgramId());
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(position);

        if (list.get(position).getProgramName().length() > 3) {
            String lettersForName = list.get(position).getProgramName().substring(0, 2);
            TextDrawable letterDrawable = TextDrawable.builder()
                    .buildRound(lettersForName, color);
            viewHolder.programImage.setImageDrawable(letterDrawable);
        } else {
            TextDrawable letterDrawable = TextDrawable.builder()
                    .buildRound(list.get(position).getProgramName(), color);
            viewHolder.programImage.setImageDrawable(letterDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
            void onClick(List<Integer> canDoTxn, int programId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView programTitle;
        ImageView programImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            programTitle = itemView.findViewById(R.id.program_title);
            programImage = itemView.findViewById(R.id.program_image);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cardview) {
                if (clickListener != null) {
                    String tag = v.getTag(R.id.holder_id).toString();
                    clickListener.onClick(list.get(getAdapterPosition()).getPuchasedItemIds(), Integer.parseInt(tag));
                }
            }
        }
    }
}
