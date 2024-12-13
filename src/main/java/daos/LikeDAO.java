package daos;

import entities.Like;
import jakarta.persistence.EntityManager;
import utils.XJPA;

import java.util.List;

public class LikeDAO {
    public boolean likeExists(long accountId, long videoId) {
        try (EntityManager em = XJPA.createEntityManager()) {
            List<Like> likes = em.createQuery("SELECT l FROM Like l WHERE l.account.id = :accountId AND l.video.id = :videoId", Like.class).setParameter("accountId", accountId).setParameter("videoId", videoId).getResultList();
            return !likes.isEmpty();
        }
    }

    public void createLike(Like like) {
        try (EntityManager em = XJPA.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(like);
            em.getTransaction().commit();
        }
    }

    public Like findLikeByAccountIdAndVideoId(long accountId, long videoId) {
        try (EntityManager em = XJPA.createEntityManager()) {
            em.getTransaction().begin();
            List<Like> likes = em.createQuery("SELECT l from Like l WHERE l.account.id = :accountId AND l.video.id = :videoId", Like.class).setParameter("accountId", accountId).setParameter("videoId", videoId).getResultList();
            if (!likes.isEmpty()) {
                return likes.get(0);
            } else {
                return null;
            }
        }
    }

    public void deleteLike(long id) {
        try (EntityManager em = XJPA.createEntityManager()) {
            Like like = em.find(Like.class, id);
            em.getTransaction().begin();
            em.remove(like);
            em.getTransaction().commit();
        }
    }
}
