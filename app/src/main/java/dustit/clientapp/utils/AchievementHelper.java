package dustit.clientapp.utils;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import dustit.clientapp.R;

public class AchievementHelper {

    public static int resolveAchievementSmallIcon(String name, int lvl) {
        switch (name) {
            case "likes":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_like_1_small;
                    case 2:
                        return R.drawable.ic_achievement_like_2_small;
                    case 3:
                        return R.drawable.ic_achievement_like_3_small;
                    case 4:
                        return R.drawable.ic_achievement_like_4_small;
                    case 5:
                        return R.drawable.ic_achievement_like_5_small;
                    case 6:
                        return R.drawable.ic_achievement_like_6_small;
                }
            case "dislikes":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_dislike_1_small;
                    case 2:
                        return R.drawable.ic_achievement_dislike_2_small;
                    case 3:
                        return R.drawable.ic_achievement_dislike_3_small;
                    case 4:
                        return R.drawable.ic_achievement_dislike_4_small;
                    case 5:
                        return R.drawable.ic_achievement_dislike_5_small;
                    case 6:
                        return R.drawable.ic_achievement_dislike_6_small;
                }
            case "comments":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_comment_1_small;
                    case 2:
                        return R.drawable.ic_achievement_comment_2_small;
                    case 3:
                        return R.drawable.ic_achievement_comment_3_small;
                    case 4:
                        return R.drawable.ic_achievement_comment_4_small;
                    case 5:
                        return R.drawable.ic_achievement_comment_5_small;
                    case 6:
                        return R.drawable.ic_achievement_comment_6_small;
                }
            case "views":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_views_1_small;
                    case 2:
                        return R.drawable.ic_achievement_views_2_small;
                    case 3:
                        return R.drawable.ic_achievement_views_3_small;
                    case 4:
                        return R.drawable.ic_achievement_views_4_small;
                    case 5:
                        return R.drawable.ic_achievement_views_5_small;
                    case 6:
                        return R.drawable.ic_achievement_views_6_small;
                    case 7:
                        return R.drawable.ic_achievement_views_7_small;
                    case 8:
                        return R.drawable.ic_achievement_views_8_small;
                }
            case "favourites":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_fav_1_small;
                    case 2:
                        return R.drawable.ic_achievement_fav_2_small;
                    case 3:
                        return R.drawable.ic_achievement_fav_3_small;
                    case 4:
                        return R.drawable.ic_achievement_fav_4_small;
                    case 5:
                        return R.drawable.ic_achievement_fav_5_small;
                    case 6:
                        return R.drawable.ic_achievement_fav_6_small;
                }
            case "referral":
                switch (lvl) {
                    case 0:
                        return R.drawable.ic_achievement_referal_1_small;
                    case 1:
                        return R.drawable.ic_achievement_referal_2_small;
                    case 2:
                        return R.drawable.ic_achievement_referal_3_small;
                    case 3:
                        return R.drawable.ic_achievement_referal_4_small;
                }
            default:
                return 0;
        }
    }

    public static int resolveAchievementIcon(String name, int level) {
        switch (name) {
            case "likes":
                switch (level) {
                    case 0:
                        return R.drawable.ic_achievement_like_0_big;
                    case 1:
                        return R.drawable.ic_achievement_like_1_big;
                    case 2:
                        return R.drawable.ic_achievement_like_2_big;
                    case 3:
                        return R.drawable.ic_achievement_like_3_big;
                    case 4:
                        return R.drawable.ic_achievement_like_4_big;
                    case 5:
                        return R.drawable.ic_achievement_like_5_big;
                    case 6:
                        return R.drawable.ic_achievement_like_6_big;
                }
            case "dislikes":
                switch (level) {
                    case 0:
                        return R.drawable.ic_achievement_dislike_0_big;
                    case 1:
                        return R.drawable.ic_achievement_dislike_1_big;
                    case 2:
                        return R.drawable.ic_achievement_dislike_2_big;
                    case 3:
                        return R.drawable.ic_achievement_dislike_3_big;
                    case 4:
                        return R.drawable.ic_achievement_dislike_4_big;
                    case 5:
                        return R.drawable.ic_achievement_dislike_5_big;
                    case 6:
                        return R.drawable.ic_achievement_dislike_6_big;
                }
            case "comments":
                switch (level) {
                    case 0:
                        return R.drawable.ic_achievement_comment_0_big;
                    case 1:
                        return R.drawable.ic_achievement_comment_1_big;
                    case 2:
                        return R.drawable.ic_achievement_comment_2_big;
                    case 3:
                        return R.drawable.ic_achievement_comment_3_big;
                    case 4:
                        return R.drawable.ic_achievement_comment_4_big;
                    case 5:
                        return R.drawable.ic_achievement_comment_5_big;
                    case 6:
                        return R.drawable.ic_achievement_comment_6_big;
                }
            case "views":
                switch (level) {
                    case 0:
                        return R.drawable.ic_achievement_views_0_big;
                    case 1:
                        return R.drawable.ic_achievement_views_1_big;
                    case 2:
                        return R.drawable.ic_achievement_views_2_big;
                    case 3:
                        return R.drawable.ic_achievement_views_3_big;
                    case 4:
                        return R.drawable.ic_achievement_views_4_big;
                    case 5:
                        return R.drawable.ic_achievement_views_5_big;
                    case 6:
                        return R.drawable.ic_achievement_views_6_big;
                    case 7:
                        return R.drawable.ic_achievement_views_7_big;
                    case 8:
                        return R.drawable.ic_achievement_views_8_big;
                }
            case "favourites":
                switch (level) {
                    case 0:
                        return R.drawable.ic_achievement_fav_0_big;
                    case 1:
                        return R.drawable.ic_achievement_fav_1_big;
                    case 2:
                        return R.drawable.ic_achievement_fav_2_big;
                    case 3:
                        return R.drawable.ic_achievement_fav_3_big;
                    case 4:
                        return R.drawable.ic_achievement_fav_4_big;
                    case 5:
                        return R.drawable.ic_achievement_fav_5_big;
                    case 6:
                        return R.drawable.ic_achievement_fav_6_big;
                }
            case "referral":
                switch (level) {
                    case 0:
                        return R.drawable.ic_referal_0_big;
                    case 1:
                        return R.drawable.ic_referal_1_big;
                    case 2:
                        return R.drawable.ic_referal_2_big;
                    case 3:
                        return R.drawable.ic_referal_3_big;
                    case 4:
                        return R.drawable.ic_referal_4_big;
                }
            default:
                return 0;
        }
    }

    public static String resolveAchievementTargetName(Resources resources, String type) {
        switch (type) {
            case "likes":
                return resources.getString(R.string.likes_achievement);
            case "dislikes":
                return resources.getString(R.string.dislikes_achievement);
            case "comments":
                return resources.getString(R.string.comments_achievement);
            case "views":
                return resources.getString(R.string.views_achievement);
            case "favourites":
                return resources.getString(R.string.favourites_achievement);
            case "referral":
                return resources.getString(R.string.referral_achievement_count);
            default:
                return "";
        }
    }

    public static List<Integer> getAchievementIcons(String name) {
        List<Integer> list = new ArrayList<>();
        switch (name) {
            case "likes": {
                list.add(R.drawable.ic_achievement_like_1_big);
                list.add(R.drawable.ic_achievement_like_2_big);
                list.add(R.drawable.ic_achievement_like_3_big);
                list.add(R.drawable.ic_achievement_like_4_big);
                list.add(R.drawable.ic_achievement_like_5_big);
                list.add(R.drawable.ic_achievement_like_6_big);
                break;
            }
            case "dislikes": {
                list.add(R.drawable.ic_achievement_dislike_1_big);
                list.add(R.drawable.ic_achievement_dislike_2_big);
                list.add(R.drawable.ic_achievement_dislike_3_big);
                list.add(R.drawable.ic_achievement_dislike_4_big);
                list.add(R.drawable.ic_achievement_dislike_5_big);
                list.add(R.drawable.ic_achievement_dislike_6_big);
                break;
            }
            case "comments": {
                list.add(R.drawable.ic_achievement_comment_1_big);
                list.add(R.drawable.ic_achievement_comment_2_big);
                list.add(R.drawable.ic_achievement_comment_3_big);
                list.add(R.drawable.ic_achievement_comment_4_big);
                list.add(R.drawable.ic_achievement_comment_5_big);
                list.add(R.drawable.ic_achievement_comment_6_big);
                break;
            }
            case "views": {
                list.add(R.drawable.ic_achievement_views_1_big);
                list.add(R.drawable.ic_achievement_views_2_big);
                list.add(R.drawable.ic_achievement_views_3_big);
                list.add(R.drawable.ic_achievement_views_4_big);
                list.add(R.drawable.ic_achievement_views_5_big);
                list.add(R.drawable.ic_achievement_views_6_big);
                list.add(R.drawable.ic_achievement_views_7_big);
                list.add(R.drawable.ic_achievement_views_8_big);
                break;
            }
            case "favourites": {
                list.add(R.drawable.ic_achievement_fav_1_big);
                list.add(R.drawable.ic_achievement_fav_2_big);
                list.add(R.drawable.ic_achievement_fav_3_big);
                list.add(R.drawable.ic_achievement_fav_4_big);
                list.add(R.drawable.ic_achievement_fav_5_big);
                list.add(R.drawable.ic_achievement_fav_6_big);
                break;
            }
            case "referral": {
                list.add(R.drawable.ic_referal_0_big);
                list.add(R.drawable.ic_referal_1_big);
                list.add(R.drawable.ic_referal_2_big);
                list.add(R.drawable.ic_referal_3_big);
                list.add(R.drawable.ic_referal_4_big);
            }
        }
        return list;
    }
}