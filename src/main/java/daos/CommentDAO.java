package daos;

import entities.Comment;
import jakarta.persistence.EntityManager;
import utils.XJPA;

import java.util.List;

public class CommentDAO {
    public List<Comment> getCommentsOfVideoId(long videoId) {
        try (EntityManager em = XJPA.createEntityManager()) {
            return em.createQuery("SELECT c FROM Comment c WHERE c.videoId = :videoId", Comment.class).getResultList();
        }
    }

    public void saveComment(Comment comment) {
        try (EntityManager em = XJPA.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
        }
    }

}
