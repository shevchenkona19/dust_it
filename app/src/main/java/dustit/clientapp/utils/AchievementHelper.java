package dustit.clientapp.utils;

import dustit.clientapp.R;

public class AchievementHelper {
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
            default:
                return 0;
        }
    }
}