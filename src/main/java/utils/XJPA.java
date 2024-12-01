package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class XJPA {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("assignment");
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
}
