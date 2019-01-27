package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.utils.GlideApp;

import static dustit.clientapp.utils.IConstants.BASE_URL;

/**
 * Created by Никита on 05.12.2017.
 */

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoriteViewHolder> {
    private final List<MemEntity> list = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public interface IFavoritesCallback {
        void onFavoriteChosen(MemEntity mem);

    }

    private IFavoritesCallback callback;

    public FavoritesRecyclerViewAdapter(Context context, IFavoritesCallback iFavoriteCallback) {
        inflater = LayoutInflater.from(context);
        callback = iFavoriteCallback;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_each_favorite, parent, false);
        return new FavoriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteViewHolder holder, int position) {
        MemEntity mem = list.get(position);
        GlideApp.with(context)
                .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.getId()))
                .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                .into(holder.ivMem);
        holder.ivMem.setOnClickListener(view -> callback.onFavoriteChosen(list.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateAll(List<MemEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void removeById(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvFavoriteImage)
        ImageView ivMem;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
