package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MemEntity> memEntityList;
    private LayoutInflater inflater;
    private Context context;
    private boolean isLoading = false;

    public FeedRecyclerViewAdapter(Context context, IFeedInteractionListener listener) {
        memEntityList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.context = context;
        interactionListener = listener;

    }

    public interface IFeedInteractionListener {
        void reloadFeedPartial(int offset);

        void reloadFeedBase();
    }

    private IFeedInteractionListener interactionListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_feed, parent, false);
                return new FeedMemViewHolder(v);
            case 1:
                if (isLoading) {
                    View v1 = inflater.inflate(R.layout.item_feed_loading, parent, false);
                    return new FeedLoadingViewHolder(v1);
                } else {
                    View v2 = inflater.inflate(R.layout.item_feed_failed_to_load, parent, false);
                    return new FeedFailedToLoadViewHolder(v2);
                }
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedMemViewHolder) {
            final FeedMemViewHolder memViewHolder = (FeedMemViewHolder) holder;
            MemEntity mem = memEntityList.get(position);
            memViewHolder.sdvMemImage.setImageURI(mem.getUrl());
            memViewHolder.tvLikeCount.setText(mem.getLikes());
            memViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_filled));
                }
            });
            memViewHolder.tvLikeCount.setText(String.valueOf(Integer.parseInt(memViewHolder.tvLikeCount.getText().toString()) + 1));
        } else if (holder instanceof FeedFailedToLoadViewHolder) {
            final FeedFailedToLoadViewHolder failedToLoadViewHolder = (FeedFailedToLoadViewHolder) holder;
            failedToLoadViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (memEntityList.size() > 1) {
                        interactionListener.reloadFeedPartial(memEntityList.size());
                    } else {
                        interactionListener.reloadFeedBase();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return memEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (memEntityList.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void onStartLoading() {
        isLoading = true;
        if (!memEntityList.contains(null)) {
            memEntityList.add(null);
            notifyItemInserted(memEntityList.size() - 1);
        } else {
            notifyItemChanged(memEntityList.size() - 1);
        }

    }

    public void onFailedToLoad() {
        isLoading = false;
        notifyItemChanged(memEntityList.size() - 1);
    }

    public void updateListWhole(List<MemEntity> list) {
        if (isLoading) {
            isLoading = false;
        }
        memEntityList.clear();
        memEntityList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateListAtEnding(List<MemEntity> list) {
        if (isLoading) {
            isLoading = false;
            int lastPos = memEntityList.size() - 1;
            memEntityList.remove(null);
            notifyItemChanged(lastPos);
        }
        int currPos = memEntityList.size() - 1;
        memEntityList.addAll(list);
        notifyItemRangeInserted(currPos, list.size());
    }

    public List<MemEntity> getList() {
        return memEntityList;
    }

    public void onItemUpdated(MemEntity memEntity) {
        String newId = memEntity.getId();
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(newId)) {
                memEntityList.remove(i);
                memEntityList.add(i, memEntity);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class FeedMemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemFeed)
        SimpleDraweeView sdvMemImage;
        @BindView(R.id.ivItemFeedIsLiked)
        ImageView ivLike;
        @BindView(R.id.tvItemFeedLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.ivItemFeedMore)
        ImageView ivMore;
        @BindView(R.id.ivItemFeedAddToFavorites)
        ImageView addToFavorites;

        FeedMemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FeedLoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbItemFeedLoading)
        ProgressBar pbLoading;

        FeedLoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }
    }

    static class FeedFailedToLoadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnItemFeedRetry)
        Button btnRetry;

        FeedFailedToLoadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
