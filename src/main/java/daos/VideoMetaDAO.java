package daos;

import entities.VideoMeta;
import jakarta.persistence.EntityManager;
import utils.XJPA;

public class VideoMetaDAO {
    public long createVideoMeta(VideoMeta videoMeta) {
        EntityManager em = XJPA.createEntityManager();
        em.getTransaction().begin();
        em.persist(videoMeta);
        em.getTransaction().commit();
        long id =  videoMeta.getId();
        em.close();
        return id;
    }
}
