package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotNull
    @Column(nullable = false, length = 100)
    private String hashPassword;

    @Column(unique = true, nullable = false, length = 30)
    @Size(max = 30, min = 4)
    private String username;

    @ManyToOne
    private Role role;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "uploader")
    private List<VideoMeta> videoMetaList;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "account")
    private List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Share> shareList = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Comment> commentList = new ArrayList<>();


    @OneToMany(mappedBy = "account")
    private List<Report> reportList = new ArrayList<>();

    public Account() {
    }


    public Account(String email, String hashPassword, String username, Role role, List<VideoMeta> videoMetaList, Date createdAt, Date updatedAt, List<Like> likeList, List<Share> shareList, List<Report> reportList) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.username = username;
        this.role = role;
        this.videoMetaList = videoMetaList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @Email String email) {
        this.email = email;
    }

    public @NotNull String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(@NotNull String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public @Size(max = 30, min = 4) String getUsername() {
        return username;
    }

    public void setUsername(@Size(max = 30, min = 4) String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<VideoMeta> getVideoMetaList() {
        return videoMetaList;
    }

    public void setVideoMetaList(List<VideoMeta> videoMetaList) {
        this.videoMetaList = videoMetaList;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
}
