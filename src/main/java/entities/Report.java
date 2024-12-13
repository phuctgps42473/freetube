package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "video_id", nullable = false)
    private VideoMeta video;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Size(min = 30, max = 1000)
    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "reported_date")
    private Date reportedDate;

    public Report() {}

    public Report(String content, Account account, VideoMeta video) {
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

    public VideoMeta getVideo() {
        return video;
    }

    public void setVideo(VideoMeta video) {
        this.video = video;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public @Size(min = 30, max = 1000) String getContent() {
        return content;
    }

    public void setContent(@Size(min = 30, max = 1000) String content) {
        this.content = content;
    }

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }
}
