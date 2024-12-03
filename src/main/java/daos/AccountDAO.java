package daos;

import entities.Account;
import jakarta.persistence.EntityManager;
import utils.XJPA;

import java.util.List;

public class AccountDAO {
    public boolean emailAndUsernameExists(String email, String username) {
        EntityManager em = XJPA.createEntityManager();
        return !em.createQuery("SELECT a FROM Account a WHERE a.email = :email AND a.username = :username", Account.class).setParameter("email", email).setParameter("username", username).getResultList().isEmpty();
    }

    public Account findAccountByEmailOrUsername(String value) {
        EntityManager em = XJPA.createEntityManager();
        List<Account> accounts = em.createQuery("SELECT a FROM Account a WHERE a.email = :value OR a.username = :value", Account.class).setParameter("value", value).getResultList();
        if (accounts.isEmpty()) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    public Account findAccountById(long id) {
        EntityManager em = XJPA.createEntityManager();
        List<Account> accounts = em.createQuery("SELECT a FROM Account a WHERE a.id = :id", Account.class).setParameter("id", id).getResultList();
        if (accounts.isEmpty()) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    public void createAccount(Account account) {
        EntityManager em = XJPA.createEntityManager();
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();
        em.close();
    }
}
