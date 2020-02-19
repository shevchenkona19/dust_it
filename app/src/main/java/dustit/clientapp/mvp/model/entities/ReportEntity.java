package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportEntity {
    @SerializedName("imageId")
    @Expose
    private int imageId;
    @SerializedName("reportReason")
    @Expose
    private String reportReason;

    public ReportEntity(int imageId, String reportReason) {
        this.imageId = imageId;
        this.reportReason = reportReason;
    }

    public ReportEntity() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }
}
