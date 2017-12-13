package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;

/**
 * Created by Никита on 11.11.2017.
 */

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommentEntity> list;
    private LayoutInflater inflater;
    private boolean isLoading = false;

    private boolean sent = false;
    private int offset = 6;
    private int lastPos;

    @Inject
    DataManager dataManager;

    public CommentsRecyclerViewAdapter(Context context, ICommentInteraction listener) {
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        interactionListener = listener;
        App.get().getAppComponent().inject(this);
    }

    public interface ICommentInteraction {
        void loadCommentsPartial(int offset);

        void loadCommentsBase();
    }

    private ICommentInteraction interactionListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_comments_comment, parent, false);
                return new CommentViewHolder(v);
            case 1:
                if (isLoading) {
                    View v1 = inflater.inflate(R.layout.item_feed_loading, parent, false);
                    return new FeedRecyclerViewAdapter.FeedLoadingViewHolder(v1);
                } else {
                    View v2 = inflater.inflate(R.layout.item_feed_failed_to_load, parent, false);
                    return new FeedRecyclerViewAdapter.FeedFailedToLoadViewHolder(v2);
                }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        if (pos % 5 == 0 && pos != 0) {
            if (pos > lastPos) {
                if (sent && pos == 5) {
                    sent = false;
                } else {
                    sent = true;
                    interactionListener.loadCommentsPartial(offset);
                    offset += 5;
                    lastPos = pos;
                }
            }
        }
        if (holder instanceof CommentViewHolder) {
            final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            CommentEntity comment = list.get(pos);
            commentViewHolder.tvUsername.setText(comment.getUsername());
            commentViewHolder.tvText.setText(comment.getText());
            commentViewHolder.sdvUserPhoto.setImageURI(Uri.parse("http://www.uni-regensburg.de/Fakultaeten/phil_Fak_II/Psychologie/Psy_II/beautycheck/english/durchschnittsgesichter/m(01-32)_gr.jpg"));
            /*commentViewHolder.sdvUserPhoto.setImageURI(IConstants.BASE_URL + "/client/getUserPhoto?token=" + dataManager.getToken() + "&targetUsername=" + comment.getUsername())*/;
            commentViewHolder.tvDateStamp.setText(comment.getDateOfPost());
        } else if (holder instanceof FeedRecyclerViewAdapter.FeedFailedToLoadViewHolder) {
            final FeedRecyclerViewAdapter.FeedFailedToLoadViewHolder failedToLoadViewHolder = (FeedRecyclerViewAdapter.FeedFailedToLoadViewHolder) holder;
            failedToLoadViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list.size() > 1) {
                        interactionListener.loadCommentsPartial(list.size());
                    } else {
                        interactionListener.loadCommentsBase();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void onStartLoading() {
        Handler handler = new Handler();
        isLoading = true;
        if (!list.contains(null)) {
            list.add(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(list.size() - 1);
                }
            }, 100);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(list.size() - 1);
                }
            }, 100);
        }
    }

    public void onFailedToLoad() {
        isLoading = false;
        notifyItemChanged(list.size() - 1);
    }

    public void updateListWhole(List<CommentEntity> list) {
        if (isLoading) {
            isLoading = false;
        }
        this.list.clear();
        this.list.addAll(list);
        lastPos = 0;
        offset = 6;
        notifyDataSetChanged();
    }

    public void updateListAtEnding(List<CommentEntity> list) {
        if (isLoading) {
            isLoading = false;
            int lastPos = this.list.size() - 1;
            this.list.remove(null);
            notifyItemChanged(lastPos);
        }
        int currPos = this.list.size() - 1;
        this.list.addAll(list);
        notifyItemRangeInserted(currPos, list.size());
    }

    public List<CommentEntity> getList() {
        return list;
    }

    public CommentEntity getItem(int position) {
        return list.get(position);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemCommentsUserPhoto)
        SimpleDraweeView sdvUserPhoto;
        @BindView(R.id.tvItemCommentUsername)
        TextView tvUsername;
        @BindView(R.id.tvItemCommentText)
        TextView tvText;
        @BindView(R.id.tvItemCommentDateStamp)
        TextView tvDateStamp;

        CommentViewHolder(View itemView) {
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
