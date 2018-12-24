package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Никита on 09.11.2017.
 */

public class CommentEntity implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("date")
    @Expose
    private String dateOfPost;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("parentId")
    @Expose
    private int parentId;

    @SerializedName("answers")
    @Expose
    private int answers;

    @SerializedName("answerUserId")
    @Expose
    private int answerUserId;

    @SerializedName("likeAchievementLvl")
    @Expose
    private int likeAchievementLvl;

    @SerializedName("dislikesAchievementLvl")
    @Expose
    private int dislikesAchievementLvl;

    @SerializedName("commentsAchievementLvl")
    @Expose
    private int commentsAchievementLvl;

    @SerializedName("favouritesAchievementLvl")
    @Expose
    private int favouritesAchievementLvl;

    @SerializedName("viewsAchievementLvl")
    @Expose
    private int viewsAchievementLvl;

    @SerializedName("firstHundred")
    @Expose
    private boolean firstHundred;

    @SerializedName("firstThousand")
    @Expose
    private boolean firstThousand;

    private List<CommentEntity> answerList;

    private boolean isExpanded = false;


    public CommentEntity(String id, int parentId, int answerUserId, int answers, String text, String dateOfPost, String username, String userId, String time, int likeAchievementLvl, int dislikesAchievementLvl, int commentsAchievementLvl, int favouritesAchievementLvl, int viewsAchievementLvl, boolean firstHundred, boolean firstThousand) {
        this.text = text;
        this.dateOfPost = dateOfPost;
        this.username = username;
        this.userId = userId;
        this.time = time;
        this.likeAchievementLvl = likeAchievementLvl;
        this.dislikesAchievementLvl = dislikesAchievementLvl;
        this.commentsAchievementLvl = commentsAchievementLvl;
        this.favouritesAchievementLvl = favouritesAchievementLvl;
        this.viewsAchievementLvl = viewsAchievementLvl;
        this.firstHundred = firstHundred;
        this.firstThousand = firstThousand;
        this.parentId = parentId;
        this.answers = answers;
        this.answerUserId = answerUserId;
        this.id = id;
    }

    protected CommentEntity(Parcel in) {
        id = in.readString();
        text = in.readString();
        dateOfPost = in.readString();
        username = in.readString();
        userId = in.readString();
        time = in.readString();
        parentId = in.readInt();
        answers = in.readInt();
        answerUserId = in.readInt();
        likeAchievementLvl = in.readInt();
        dislikesAchievementLvl = in.readInt();
        commentsAchievementLvl = in.readInt();
        favouritesAchievementLvl = in.readInt();
        viewsAchievementLvl = in.readInt();
        firstHundred = in.readByte() != 0;
        firstThousand = in.readByte() != 0;
        answerList = in.createTypedArrayList(CommentEntity.CREATOR);
        isExpanded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(dateOfPost);
        dest.writeString(username);
        dest.writeString(userId);
        dest.writeString(time);
        dest.writeInt(parentId);
        dest.writeInt(answers);
        dest.writeInt(answerUserId);
        dest.writeInt(likeAchievementLvl);
        dest.writeInt(dislikesAchievementLvl);
        dest.writeInt(commentsAchievementLvl);
        dest.writeInt(favouritesAchievementLvl);
        dest.writeInt(viewsAchievementLvl);
        dest.writeByte((byte) (firstHundred ? 1 : 0));
        dest.writeByte((byte) (firstThousand ? 1 : 0));
        dest.writeTypedList(answerList);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentEntity> CREATOR = new Creator<CommentEntity>() {
        @Override
        public CommentEntity createFromParcel(Parcel in) {
            return new CommentEntity(in);
        }

        @Override
        public CommentEntity[] newArray(int size) {
            return new CommentEntity[size];
        }
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateOfPost() {
        return dateOfPost;
    }

    public void setDateOfPost(String dateOfPost) {
        this.dateOfPost = dateOfPost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikeAchievementLvl() {
        return likeAchievementLvl;
    }

    public void setLikeAchievementLvl(int likeAchievementLvl) {
        this.likeAchievementLvl = likeAchievementLvl;
    }

    public int getDislikesAchievementLvl() {
        return dislikesAchievementLvl;
    }

    public void setDislikesAchievementLvl(int dislikesAchievementLvl) {
        this.dislikesAchievementLvl = dislikesAchievementLvl;
    }

    public int getCommentsAchievementLvl() {
        return commentsAchievementLvl;
    }

    public void setCommentsAchievementLvl(int commentsAchievementLvl) {
        this.commentsAchievementLvl = commentsAchievementLvl;
    }

    public int getFavouritesAchievementLvl() {
        return favouritesAchievementLvl;
    }

    public void setFavouritesAchievementLvl(int favouritesAchievementLvl) {
        this.favouritesAchievementLvl = favouritesAchievementLvl;
    }

    public int getViewsAchievementLvl() {
        return viewsAchievementLvl;
    }

    public void setViewsAchievementLvl(int viewsAchievementLvl) {
        this.viewsAchievementLvl = viewsAchievementLvl;
    }

    public boolean getFirstHundred() {
        return firstHundred;
    }

    public void setFirstHundred(boolean firstHundred) {
        this.firstHundred = firstHundred;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public boolean isFirstHundred() {
        return firstHundred;
    }

    public boolean isFirstThousand() {
        return firstThousand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getFirstThousand() {
        return firstThousand;
    }

    public void setFirstThousand(boolean firstThousand) {
        this.firstThousand = firstThousand;
    }

    public int getAnswerUserId() {
        return answerUserId;
    }

    public void setAnswerUserId(int answerUserId) {
        this.answerUserId = answerUserId;
    }

    public List<CommentEntity> getAnswerList() {
        if (answerList == null) {
            answerList = new ArrayList<>();
        }
        return answerList;
    }

    public void setAnswerList(List<CommentEntity> answerList) {
        this.answerList.clear();
        this.answerList.addAll(answerList);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
