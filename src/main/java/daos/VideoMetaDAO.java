package daos;

import entities.VideoMeta;
import jakarta.persistence.EntityManager;
import utils.XJPA;

import java.util.List;

public class VideoMetaDAO {
    public long createVideoMeta(VideoMeta videoMeta) {
        EntityManager em = XJPA.createEntityManager();
        em.getTransaction().begin();
        em.persist(videoMeta);
        em.getTransaction().commit();
        long id = videoMeta.getId();
        em.close();
        return id;
    }

    public List<VideoMeta> findVideoMetas(int offset, int length) {
        try (EntityManager em = XJPA.createEntityManager()) {
            return em.createQuery("select v FROM VideoMeta v", VideoMeta.class).setFirstResult(offset).setMaxResults(length).getResultList();
        }
    }

    public VideoMeta findById(long id) {
        try (EntityManager em = XJPA.createEntityManager()) {
            List<VideoMeta> videos = em.createQuery("SELECT v FROM VideoMeta v WHERE v.id = :id", VideoMeta.class).setParameter("id", id).getResultList();
            if (!videos.isEmpty()) {
                return videos.get(0);
            } else {
                return null;
            }
        }
    }

    public List<VideoMeta> findAllBut(long id) {
        try (EntityManager em = XJPA.createEntityManager()) {
            return em.createQuery("SELECT v FROM VideoMeta v WHERE v.id != :id", VideoMeta.class).setParameter("id", id).setMaxResults(12).getResultList();
        }
    }

    public List<VideoMeta> findFromUserId(long userId) {
        try (EntityManager em = XJPA.createEntityManager()) {
            return em.createQuery("SELECT v FROM VideoMeta v WHERE v.uploader.id = :userId", VideoMeta.class).setParameter("userId", userId).getResultList();
        }
    }
}
