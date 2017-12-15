package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.utils.IConstants;

/**
 * Created by Никита on 05.12.2017.
 */

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoriteViewHolder> {
    private List<FavoriteEntity> list;
    private LayoutInflater inflater;
    private String token;


    public interface IFavoritesCallback {
        void onFavoriteChosen(String id);
        void onFavoriteSelected(String id);
    }

    private IFavoritesCallback callback;

    public FavoritesRecyclerViewAdapter(Context context, IFavoritesCallback iFavoriteCallback, String token) {
        inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
        this.token = token;
        callback = iFavoriteCallback;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_each_favorite, parent, false);
        return new FavoriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
        holder.sdvImage.setImageURI(IConstants.BASE_URL + "/client/getFavorite?token=" + token + "&id=" + list.get(position).getId());
        /*holder.sdvImage.setImageURI(list.get(position).getId());*/
        holder.sdvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.onFavoriteChosen(list.get(holder.getAdapterPosition()).getId());
            }
        });
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
        SimpleDraweeView sdvImage;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
