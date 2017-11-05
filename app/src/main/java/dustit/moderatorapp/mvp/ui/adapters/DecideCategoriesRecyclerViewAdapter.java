package dustit.moderatorapp.mvp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.moderatorapp.R;
import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.Category;

public class DecideCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<DecideCategoriesRecyclerViewAdapter.DecideItemViewHolder> {

    private List<Category> categoryList;
    private LayoutInflater inflater;

    public DecideCategoriesRecyclerViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        categoryList = new ArrayList<>();
    }

    @Override
    public DecideItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_decide_category, parent, false);
        return new DecideItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DecideItemViewHolder holder, int position) {
        final Category category = categoryList.get(position);
        if (category.isChecked()) {
            holder.cbCategory.setChecked(true);
        } else {
            holder.cbCategory.setChecked(false);
        }
        holder.cbCategory.setText(category.getName());
        holder.cbCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                category.setChecked(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateList(List<Category> categories) {
        categoryList.clear();
        categoryList.addAll(categories);
        notifyDataSetChanged();
    }

    public CategoriesIdEntity getCheckedCategories() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).isChecked()) {
                Category category = categoryList.get(i);
                builder.append(category.getId());
                builder.append(" ");
                Log.d("MY", "Builder: " + builder.toString());
            }
        }
        return new CategoriesIdEntity(builder.toString());
    }

    public void resetChecked() {
        for (Category category :
                categoryList) {
            category.setChecked(false);
        }
        notifyDataSetChanged();
    }

    static class DecideItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbItemDecideCategory)
        CheckBox cbCategory;

        DecideItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
