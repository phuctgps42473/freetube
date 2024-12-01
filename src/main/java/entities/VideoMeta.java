package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "video_metas")
public class VideoMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 1, max = 255)
    @NotNull
    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String title;

    @Size(min = 1, max = 2000)
    @Column(columnDefinition = "nvarchar(2000)")
    private String description;

    @Column(name = "thumbnail")
    private String thumbnail;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account uploader;

    @CreationTimestamp
    @Column(updatable = false)
    private Date uploadDate;

    @Min(0)
    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    @Min(0)
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Min(0)
    @Column(name = "share_count", nullable = false)
    private int shareCount = 0;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "video")
    private List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Share> shareList = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Comment> Comment = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Report> reportList = new ArrayList<>();

    public VideoMeta() {
    }

    public VideoMeta(String title, String description, Account uploader, String thumbnail) {
        this.title = title;
        this.description = description;
        this.uploader = uploader;
        this.thumbnail = thumbnail;
    }


    @Min(0)
    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(@Min(0) long viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getUploader() {
        return uploader;
    }

    public void setUploader(Account uploader) {
        this.uploader = uploader;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Min(0)
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(@Min(0) int likeCount) {
        this.likeCount = likeCount;
    }

    @Min(0)
    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(@Min(0) int shareCount) {
        this.shareCount = shareCount;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    public List<Share> getShareList() {
        return shareList;
    }

    public void setShareList(List<Share> shareList) {
        this.shareList = shareList;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<entities.Comment> getComment() {
        return Comment;
    }

    public void setComment(List<entities.Comment> comment) {
        Comment = comment;
    }
}
