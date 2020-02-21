package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.GlideRequests;

import static dustit.clientapp.utils.IConstants.BASE_URL;

/**
 * Created by Никита on 05.12.2017.
 */

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoriteViewHolder> {
    private final List<MemEntity> list = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private List<MemEntity> removedList = new ArrayList<>();
    private boolean isMe;
    private IFavoritesCallback callback;
    private GlideRequests glide;

    public FavoritesRecyclerViewAdapter(Context context, IFavoritesCallback iFavoriteCallback) {
        inflater = LayoutInflater.from(context);
        callback = iFavoriteCallback;
        glide = GlideApp.with(context);
        this.context = context;
    }

    public void refreshListWithMem(RefreshedMem refreshedMem) {
        if (isMe) {
            if (!refreshedMem.isFavourite()) {
                MemEntity memEntity = removeById(refreshedMem.getId());
                memEntity = refreshedMem.populateMemEntity(memEntity);
                if (memEntity != null) {
                    removedList.add(memEntity);
                }
            } else {
                for (MemEntity mem : removedList) {
                    if (mem.getId() == refreshedMem.getId()) {
                        mem = refreshedMem.populateMemEntity(mem);
                        list.add(0, mem);
                        removedList.remove(mem);
                        notifyItemInserted(0);
                        return;
                    }
                }
                for (MemEntity mem : list) {
                    if (mem.getId() == refreshedMem.getId()) {
                        refreshedMem.populateMemEntity(mem);
                        return;
                    }
                }
                callback.reload();
            }
        }
    }

    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        if (isMe) {
            if (!restoreMemEntity.isFavourite()) {
                MemEntity memEntity = removeById(restoreMemEntity.getId());
                memEntity = restoreMemEntity.populateMemEntity(memEntity);
                if (memEntity != null) {
                    removedList.add(memEntity);
                }
            } else {
                for (MemEntity mem : removedList) {
                    if (mem.getId() == restoreMemEntity.getId()) {
                        mem = restoreMemEntity.populateMemEntity(mem);
                        list.add(0, mem);
                        removedList.remove(mem);
                        notifyItemInserted(0);
                    }
                }
                for (MemEntity mem : list) {
                    if (mem.getId() == restoreMemEntity.getId()) {
                        restoreMemEntity.populateMemEntity(mem);
                        return;
                    }
                }
                callback.reload();
            }
        }
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
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
        glide
                .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.getId()))
                .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                .into(holder.ivMem);
        holder.ivMem.setOnClickListener(view -> callback.onFavoriteChosen(mem, isMe));
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

    @Nullable
    private MemEntity removeById(int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                notifyItemRemoved(i);
                return list.remove(i);
            }
        }
        return null;
    }

    public interface IFavoritesCallback {
        void onFavoriteChosen(MemEntity mem, boolean isMe);

        void reload();
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
