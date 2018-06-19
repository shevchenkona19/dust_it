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
import android.widget.RelativeLayout
import com.facebook.drawee.backends.pipeline.Fresco
import dustit.clientapp.R
import dustit.clientapp.mvp.model.entities.FavoriteEntity
import dustit.clientapp.mvp.model.entities.MemEntity
import dustit.clientapp.mvp.model.entities.RefreshedMem
import dustit.clientapp.mvp.model.entities.RestoreMemEntity
import dustit.clientapp.utils.DoubleClickListener
import dustit.clientapp.utils.IConstants.BASE_URL
import dustit.clientapp.utils.IConstants.OPINION.*
import dustit.clientapp.utils.L
import dustit.clientapp.utils.containers.Pair
import kotlinx.android.synthetic.main.item_feed.view.*
import kotlinx.android.synthetic.main.item_feed_failed_to_load.view.*
import java.util.*

class FeedRecyclerViewAdapter(rv: RecyclerView,
                              private val feedInteractionListener: IFeedInteractionListener,
                              private val context: Context,
                              private val appBarHeight: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val entityList: ArrayList<MemEntity?> = ArrayList()
    private val favoriteEntityList: ArrayList<FavoriteEntity> = ArrayList()
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var isLoading: Boolean = true
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0
    private var isMemesEnded: Boolean = false

    private val visibleThreshold: Byte = 2

    interface IFeedInteractionListener {
        fun reloadFeedBase()
        fun onMemSelected(animStart: View, mem: MemEntity)
        fun postLike(mem: MemEntity)
        fun deleteLike(mem: MemEntity)
        fun postDislike(mem: MemEntity)
        fun deleteDislike(mem: MemEntity)
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
                if (!isMemesEnded) {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        feedInteractionListener.loadMore(entityList.size - 1)
                        isLoading = true
                    }
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
            if (mem.isFavorite) {
                holder.itemView.ivItemFeedAddToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_added_to_favourites))
            } else {
                holder.itemView.ivItemFeedAddToFavorites.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_to_favourites))
            }
            when (opinion) {
                LIKED -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_pressed))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike))
                }
                DISLIKED -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike_pressed))
                }
                NEUTRAL -> {
                    holder.itemView.ivItemFeedIsLiked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like))
                    holder.itemView.ivItemFeedDisliked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dislike))
                }
            }
            holder.itemView.ivItemFeedIsLiked.setOnClickListener {
                when (opinion) {
                    LIKED -> {
                        mem.setLikes(-1)
                        mem.opinion = NEUTRAL
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.deleteLike(mem)
                    }
                    DISLIKED -> {
                        mem.setDislikes(-1)
                        mem.setLikes(1)
                        mem.opinion =LIKED
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.postLike(mem)
                    }
                    NEUTRAL -> {
                        mem.setLikes(1)
                        mem.opinion = LIKED
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.postLike(mem)
                    }
                }
            }
            holder.itemView.ivItemFeedDisliked.setOnClickListener {
                when (opinion) {
                   DISLIKED -> {
                        mem.setDislikes(-1)
                        mem.opinion = NEUTRAL
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.deleteDislike(mem)
                    }
                    LIKED -> {
                        mem.setLikes(-1)
                        mem.setDislikes(1)
                        mem.opinion = DISLIKED
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.postDislike(mem)
                    }
                    NEUTRAL -> {
                        mem.setDislikes(1)
                        mem.opinion = DISLIKED
                        notifyItemChanged(holder.adapterPosition)
                        feedInteractionListener.postDislike(mem)
                    }
                }
            }
            holder.itemView.sdvItemFeed.setOnClickListener(object : DoubleClickListener() {
                override fun onSingleClick(view: View) {
                    feedInteractionListener.onMemSelected(holder.itemView, mem)
                }

                override fun onDoubleClick(v: View) {
                    when (opinion) {
                        DISLIKED, NEUTRAL -> feedInteractionListener.postLike(mem)
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
        findAndReloadFavorites()
    }

    fun updateListAtEnding(list: List<MemEntity>) {
        isLoading = false
        val currPos = entityList.size - 2
        entityList.addAll(list)
        notifyItemRangeInserted(currPos, list.size)
        findAndReloadFavorites()
    }

    private fun findAndReloadFavorites() {
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

    fun memesEnded() {
        isMemesEnded = true
    }

    fun refreshMem(refreshedMem: RefreshedMem) {
        val pair: Pair<Int, MemEntity>? = findMemAndPositionById(refreshedMem.id)
        if (pair != null) {
            pair.mem.likes = refreshedMem.likes
            pair.mem.dislikes = refreshedMem.dislikes
            pair.mem.opinion = refreshedMem.opinion
            L.print("Notify!")
            notifyItemChanged(pair.position)
        }
    }

    fun restoreMem(restoreMemEntity: RestoreMemEntity) {
        val memAndPos: Pair<Int, MemEntity>? = findMemAndPositionById(restoreMemEntity.id)
        if (memAndPos != null) {
            val mem: MemEntity = memAndPos.mem
            val pos = memAndPos.position
            mem.likes = restoreMemEntity.likes
            mem.dislikes = restoreMemEntity.dislikes
            mem.opinion = restoreMemEntity.opinion
            notifyItemChanged(pos)
        }
    }

    internal class MemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var isMoreLayoutVisible = false

        fun bind(mem: MemEntity) {
            isMoreLayoutVisible = false
            itemView.clItemFeedMoreLayout.visibility = View.GONE
            itemView.sdvItemFeed.aspectRatio = mem.width.toFloat() / mem.height
            itemView.sdvItemFeed.controller = Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setUri(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.id))
                    .build()
            itemView.tvItemFeedSrc.text = mem.source
            itemView.sdvItemFeed.setImageURI(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.id))
            itemView.tvItemFeedLikeCount.text = mem.likes
            itemView.tvItemFeedDislikeCount.text = mem.dislikes

        }
    }

    internal class FeedLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class FeedFailedToLoadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listener: () -> Unit) {
            itemView.btnItemFeedRetry.setOnClickListener { listener() }
        }
    }
}