package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.PersonalCategory;

public class ChangeCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<ChangeCategoriesRecyclerViewAdapter.RowViewHolder> {
    private final List<PersonalCategory> categories = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ChangeCategoriesRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(layoutInflater.inflate(R.layout.item_change_category_each, parent, false));
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        final PersonalCategory category = categories.get(position);
        holder.cbItem.setText(category.getCategoryName());
        holder.cbItem.setChecked(category.isChecked());
        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> category.setChecked(isChecked));
    }

    public void updateList(List<PersonalCategory> list) {
        categories.clear();
        categories.addAll(list);
        notifyDataSetChanged();
    }

    public String[] getChecked() {
        final List<String> list = new ArrayList<>();
        for (PersonalCategory c :
                categories) {
            if (c.isChecked()) {
                list.add(c.getCategoryId());
            }
        }
        final String[] arr = new String[list.size()];
        return list.toArray(arr);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class RowViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbChangeCategoryItem)
        CheckBox cbItem;

        RowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
