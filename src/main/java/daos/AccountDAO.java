package daos;

import entities.Account;
import jakarta.persistence.EntityManager;
import utils.XJPA;

import java.util.List;

public class AccountDAO {
    public List<Account> findAll() {
        try (EntityManager em = XJPA.createEntityManager()) {
            return em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
        }
    }

    public void update(Account account) {
        try (EntityManager em = XJPA.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        }
    }

    public boolean emailOrUsernameExists(String email, String username) {
        try (EntityManager em = XJPA.createEntityManager()) {
            return !em.createQuery("SELECT a FROM Account a WHERE a.email = :email Or a.username = :username", Account.class).setParameter("email", email).setParameter("username", username).getResultList().isEmpty();
        }
    }

    public Account findAccountByEmailOrUsername(String value) {
        try (EntityManager em = XJPA.createEntityManager()) {
            List<Account> accounts = em.createQuery("SELECT a FROM Account a WHERE a.email = :value OR a.username = :value", Account.class).setParameter("value", value).getResultList();
            if (accounts.isEmpty()) {
                return null;
            } else {
                return accounts.get(0);
            }
        }
    }

    public Account findAccountById(long id) {
        try (EntityManager em = XJPA.createEntityManager()) {
            List<Account> accounts = em.createQuery("SELECT a FROM Account a WHERE a.id = :id", Account.class).setParameter("id", id).getResultList();
            if (accounts.isEmpty()) {
                return null;
            } else {
                return accounts.get(0);
            }
        }
    }

    public void createAccount(Account account) {
        try (EntityManager em = XJPA.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        }
    }
}
