package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private
    @NotNull
    @Size(max = 2000)
    @Column(name = "content", columnDefinition = "nvarchar(2000)", nullable = false)
    String content;

    private
    @CreationTimestamp
    @Column(name = "created_at")
    Date createdAt;

    private
    @Min(0)
    @Max(3)
    @Column(name = "update_count")
    int updateCount = 0;

    private
    @Column(name = "is_deleted")
    boolean isDeleted = false;

    private
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    Account account;

    private
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "video_id")
    VideoMeta video;

    public Comment() {}

    public Comment(String content, Account account, VideoMeta video) {
        this.content = content;
        this.account = account;
        this.video = video;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull @Size(max = 2000) String getContent() {
        return content;
    }

    public void setContent(@NotNull @Size(max = 2000) String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Min(0)
    @Max(3)
    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(@Min(0) @Max(3) int updateCount) {
        this.updateCount = updateCount;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public VideoMeta getVideo() {
        return video;
    }

    public void setVideo(VideoMeta video) {
        this.video = video;
    }
}
