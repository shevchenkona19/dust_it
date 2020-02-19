package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;

/**
 * Created by shevc on 04.10.2017.
 * Let's GO!
 */

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ResultViewHolder> {
    private List<Category> categoryList;
    private LayoutInflater inflater;
    private boolean useDarkColor = false;


    public CategoriesRecyclerViewAdapter(Context context) {
        categoryList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public CategoriesRecyclerViewAdapter(LayoutInflater inflater) {
        categoryList = new ArrayList<>();
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ResultViewHolder holder, int position) {
        holder.cbTheme.setText(categoryList.get(position).getName());
        holder.cbTheme.setChecked(categoryList.get(position).isChecked());
        if (useDarkColor) {
            holder.cbTheme.setTextColor(Color.parseColor("#000000"));
        }
        holder.cbTheme.setOnCheckedChangeListener((buttonView, isChecked) -> categoryList.get(holder.getAdapterPosition()).setChecked(isChecked));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateItems(List<Category> list) {
        categoryList.clear();
        categoryList.addAll(list);
        notifyDataSetChanged();
    }

    public void setChecks(String[] ids) {
        for (String name :
                ids) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getName().equals(name)) {
                    categoryList.get(i).setChecked(true);
                    break;
                }
            }
        }
    }

    public String getChecked() {
        StringBuilder builder = new StringBuilder();
        for (Category cat : categoryList) {
            if (cat.isChecked()) {
                builder.append(cat.getId());
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public void useDarkColor() {
        useDarkColor = true;
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbItemResult)
        CheckBox cbTheme;

        ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
