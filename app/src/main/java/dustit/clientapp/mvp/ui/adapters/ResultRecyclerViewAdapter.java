package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.ThemeEntity;

/**
 * Created by shevc on 04.10.2017.
 * Let's GO!
 */

public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<ResultRecyclerViewAdapter.ResultViewHolder>{
    private List<ThemeEntity> themeEntities;
    private LayoutInflater inflater;


    public ResultRecyclerViewAdapter(Context context, List<ThemeEntity> list) {
        themeEntities = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.cbTheme.setText(themeEntities.get(position).getName());
        holder.cbTheme.setChecked(themeEntities.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return themeEntities.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbItemResult)
        CheckBox cbTheme;

        public ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
