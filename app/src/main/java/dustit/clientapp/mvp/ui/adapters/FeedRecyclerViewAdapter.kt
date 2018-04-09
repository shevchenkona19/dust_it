package dustit.clientapp.mvp.ui.adapters

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import dustit.clientapp.R
import dustit.clientapp.mvp.model.entities.FavoriteEntity
import dustit.clientapp.mvp.model.entities.MemEntity
import dustit.clientapp.utils.DoubleClickListener
import dustit.clientapp.utils.IConstants
import dustit.clientapp.utils.containers.Pair
import kotlinx.android.synthetic.main.item_feed.view.*


class FeedRecyclerViewAdapter(rv: RecyclerView,private val feedInteractionListener: IFeedInteractionListener, private val context: Context, private val appBarHeight: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val entityList: ArrayList<MemEntity?> = ArrayList()
    private val favoriteEntityList: ArrayList<FavoriteEntity> = ArrayList()
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var isLoading: Boolean = true
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0

    private val visibleThreshold: Byte = 2

    interface IFeedInteractionListener {
        fun reloadFeedBase()
        fun onMemSelected(animStart: View, mem: MemEntity)
        fun postLike(id: String)
        fun deleteLike(id: String)
        fun postDislike(id: String)
        fun deleteDislike(id: String)
        fun addToFavorites(id: String)
        fun deleteFromFavorites(id: String)
        fun showErrorToast()
        fun loadMore(offset: Int)
    }

    init {
        val linearLayoutManager: LinearLayoutManager = rv.layoutManager as LinearLayoutManager
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    feedInteractionListener.loadMore(entityList.size - 1)
                    isLoading = true
                }
            }
        })
        entityList.add(null)
    }

    private val post = 0
    private val loading = 1
    private val failedToLoad = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            post -> MemViewHolder(layoutInflater.inflate(R.layout.item_feed, parent, false))
            loading -> FeedLoadingViewHolder(layoutInflater.inflate(R.layout.item_feed_loading, parent, false))
            failedToLoad -> FeedFailedToLoadViewHolder(layoutInflater.inflate(R.layout.item_feed_failed_to_load, parent, false))
            else -> MemViewHolder(layoutInflater.inflate(R.layout.item_feed_loading, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return entityList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapterPosition: Int = holder.adapterPosition
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.itemView.layoutParams = if (adapterPosition == 0) {
            params.setMargins(0, appBarHeight, 0, 0)
            params
        } else {
            params.setMargins(0, 0, 0, 0)
            params
        }
        if (holder is MemViewHolder) {
            val mem: MemEntity = entityList[adapterPosition]!!
            val opinion = mem.opinion
            holder.bind(mem)
            holder.itemView.sdvItemFeed.hierarchy.setRetryImage(ContextCompat.getDrawable(context, R.drawable.ic_reload))
            if (mem.isFavorite) {
                holder.itemView.ivItemFeedAddToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_added_to_favourites))
            } else {
                holder.itemView.ivItemFeedAddToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_to_favourites))
            }
            when (opinion) {
                IConstants.OPINION.LIKED -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_pressed))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike))
                }
                IConstants.OPINION.DISLIKED -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike_pressed))
                }
                IConstants.OPINION.NEUTRAL -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike))
                }
            }
            holder.itemView.ivItemFeedIsLiked.setOnClickListener {
                when (opinion) {
                    IConstants.OPINION.LIKED -> feedInteractionListener.deleteLike(mem.id)
                    IConstants.OPINION.DISLIKED, IConstants.OPINION.NEUTRAL -> feedInteractionListener.postLike(mem.id)
                }
            }
            holder.itemView.ivItemFeedDisliked.setOnClickListener {
                when (opinion) {
                    IConstants.OPINION.DISLIKED -> feedInteractionListener.deleteDislike(mem.id)
                    IConstants.OPINION.LIKED, IConstants.OPINION.NEUTRAL -> feedInteractionListener.postDislike(mem.id)
                }
            }
            holder.itemView.sdvItemFeed.setOnClickListener(object : DoubleClickListener() {
                override fun onSingleClick(view: View) {
                    feedInteractionListener.onMemSelected(holder.itemView, mem)
                }

                override fun onDoubleClick(v: View) {
                    when (opinion) {
                        IConstants.OPINION.DISLIKED, IConstants.OPINION.NEUTRAL -> feedInteractionListener.postLike(mem.id)
                        else -> {
                        }
                    }
                }
            })
            holder.itemView.ivItemFeedAddToFavorites.setOnClickListener {
                if (mem.isFavorite) {
                    feedInteractionListener.deleteFromFavorites(mem.id)
                } else {
                    feedInteractionListener.addToFavorites(mem.id)
                }
            }
        }
        if (holder is FeedFailedToLoadViewHolder) {
            holder.bind {
                if (entityList.size > 1) {
                    feedInteractionListener.loadMore(entityList.size - 1)
                } else {
                    feedInteractionListener.reloadFeedBase()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (entityList[position] == null) {
            if (isLoading) loading else failedToLoad
        } else {
            post
        }
    }

    fun onFailedToLoad() {
        isLoading = false
        notifyItemChanged(entityList.size - 1)
    }

    fun updateListWhole(list: List<MemEntity>) {
        isLoading = false
        entityList.clear()
        entityList.addAll(list)
        notifyDataSetChanged()
        findAndReload()
    }

    fun updateListAtEnding(list: List<MemEntity>) {
        isLoading = false
        val currPos = entityList.size - 2
        entityList.addAll(list)
        notifyItemRangeInserted(currPos, list.size)
        findAndReload()
    }

    private fun findAndReload() {
        if (entityList.size == 0) return
        for (i in favoriteEntityList.indices) {
            val pair = findMemAndPositionById(favoriteEntityList[i].id)
            if (pair != null) {
                pair.mem.isFavorite = true
                notifyItemChanged(pair.position)
            }
        }
    }

    private fun findMemAndPositionById(id: String): Pair<Int, MemEntity>? {
        var pair: Pair<Int, MemEntity>? = null
        for (i in entityList.indices) {
            if (entityList[i]?.id == id) {
                val mem = entityList[i]
                pair = Pair(i, mem!!)
                break
            }
        }
        return pair
    }

    fun setFavoritesList(list: List<FavoriteEntity>) {
        favoriteEntityList.clear()
        favoriteEntityList.addAll(list)
    }

    fun addedToFavorites(id: String) {
        val pair = findMemAndPositionById(id)
        val mem = pair!!.mem
        val pos = pair.position
        mem.isFavorite = true
        notifyItemChanged(pos)
    }

    fun onDeletedFromFavorites(id: String) {
        val pair = findMemAndPositionById(id)
        val mem = pair!!.mem
        val pos = pair.position
        mem.isFavorite = false
        notifyItemChanged(pos)
    }

    fun onLikePostedSuccesfully(id: String) {
        val p = findMemAndPositionById(id)
        val mem = p!!.mem
        val pos = p.position
        val opinion = mem.opinion
        if (opinion != null) {
            if (opinion == IConstants.OPINION.DISLIKED) mem.setDislikes(-1)
            mem.setLikes(1)
            mem.opinion = IConstants.OPINION.LIKED
            notifyItemChanged(pos)
        } else {
            showErrorToast()
        }
    }

    fun onLikeDeletedSuccesfully(id: String) {
        val pair = findMemAndPositionById(id)
        val mem = pair!!.mem
        val pos = pair.position
        mem.setLikes(-1)
        mem.opinion = IConstants.OPINION.NEUTRAL
        notifyItemChanged(pos)
    }

    fun onDislikePostedSuccesfully(id: String) {
        val pair = findMemAndPositionById(id)
        val mem = pair!!.mem
        val pos = pair.position
        val opinion = mem.opinion
        if (opinion != null) {
            if (opinion == IConstants.OPINION.LIKED) mem.setLikes(-1)
            mem.setDislikes(1)
            mem.opinion = IConstants.OPINION.DISLIKED
            notifyItemChanged(pos)
        } else {
            showErrorToast()
        }
    }

    fun onDislikeDeletedSuccesfully(id: String) {
        val pair = findMemAndPositionById(id)
        val mem = pair!!.mem
        val pos = pair.position
        mem.setDislikes(-1)
        mem.opinion = IConstants.OPINION.NEUTRAL
        notifyItemChanged(pos)
    }

    private fun showErrorToast() {
        feedInteractionListener.showErrorToast()
    }

    internal class MemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var isMoreLayoutVisible = false

        fun bind(mem: MemEntity) {
            isMoreLayoutVisible = false
            itemView.clItemFeedMoreLayout.visibility = View.GONE
            itemView.sdvItemFeed.hierarchy.setProgressBarImage(ProgressBarDrawable())
            itemView.sdvItemFeed.controller = Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setUri(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + mem.id))
                    .build()
            itemView.tvItemFeedSrc.text = mem.source
            itemView.sdvItemFeed.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + mem.id))
            itemView.tvItemFeedLikeCount.text = mem.likes
            itemView.tvItemFeedDislikeCount.text = mem.dislikes
            itemView.ivItemFeedMore.setOnClickListener({
                TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                itemView.clItemFeedMoreLayout.visibility = if (isMoreLayoutVisible) View.GONE else View.VISIBLE
                isMoreLayoutVisible = !isMoreLayoutVisible
            })
        }
    }

    internal class FeedLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.pbItemFeedLoading)
        lateinit var pbLoading: ProgressBar

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    internal class FeedFailedToLoadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.btnItemFeedRetry)
        lateinit var btnRetry: Button

        fun bind(listener: () -> Unit) {
            btnRetry.setOnClickListener { listener() }
        }

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}