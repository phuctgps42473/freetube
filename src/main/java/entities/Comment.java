package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    Date created_at;

    private
    @Min(0)
    @Max(3)
    @Column(name = "update_count")
    int updateCount = 0;

    private
    @Column(name = "is_deleted")
    boolean is_deleted = false;

    private
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    Account account;

    private
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id")
    VideoMeta video;
}
