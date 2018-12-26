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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;

import static dustit.clientapp.utils.IConstants.BASE_URL;

/**
 * Created by Никита on 05.12.2017.
 */

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoriteViewHolder> {
    private final List<FavoriteEntity> list = new ArrayList<>();
    private LayoutInflater inflater;


    public interface IFavoritesCallback {
        void onFavoriteChosen(String id);

    }

    private IFavoritesCallback callback;

    public FavoritesRecyclerViewAdapter(Context context, IFavoritesCallback iFavoriteCallback) {
        inflater = LayoutInflater.from(context);
        callback = iFavoriteCallback;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_each_favorite, parent, false);
        return new FavoriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteViewHolder holder, int position) {
        GlideApp.with(holder.itemView)
                .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + list.get(position).getId()))
                .placeholder(new ColorDrawable(ContextCompat.getColor(holder.itemView.getContext(), R.color.placeholder_color)))
                .into(holder.sdvImage);
        holder.sdvImage.setOnClickListener(view -> callback.onFavoriteChosen(list.get(holder.getAdapterPosition()).getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateAll(List<FavoriteEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void removeById(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvFavoriteImage)
        ImageView sdvImage;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
