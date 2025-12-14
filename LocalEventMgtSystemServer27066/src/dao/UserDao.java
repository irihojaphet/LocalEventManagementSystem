package dao;

import java.util.List;
import model.User;
import org.hibernate.*;
import org.hibernate.query.Query;

/**
 * User DAO with Hibernate implementation
 * 
 * @author 27066
 */
public class UserDao {
    
    // CREATE
    public User createUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // UPDATE
    public User updateUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // DELETE
    public User deleteUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID
    public User findUserById(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            User user = ss.get(User.class, userObj.getUserId());
            ss.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by Username
    public User findUserByUsername(String username) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = ss.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();
            ss.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Login
    public User login(String username, String hashedPassword) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = ss.createQuery(
                "FROM User WHERE username = :username AND password = :password AND accountStatus = 'active'", 
                User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", hashedPassword);
            User user = query.uniqueResult();
            
            if (user != null) {
                // Update last login
                Transaction tr = ss.beginTransaction();
                user.setLastLogin(new java.sql.Timestamp(System.currentTimeMillis()));
                ss.update(user);
                tr.commit();
            }
            
            ss.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Check if username exists
    public boolean isUsernameExists(String username) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = ss.createQuery(
                "SELECT COUNT(*) FROM User WHERE username = :username", 
                Long.class
            );
            query.setParameter("username", username);
            Long count = query.uniqueResult();
            ss.close();
            return count != null && count > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    // READ - Check if email exists
    public boolean isEmailExists(String email) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = ss.createQuery(
                "SELECT COUNT(*) FROM User WHERE email = :email", 
                Long.class
            );
            query.setParameter("email", email);
            Long count = query.uniqueResult();
            ss.close();
            return count != null && count > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    // READ - Find all users
    public List<User> findAllUsers() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = ss.createQuery("FROM User ORDER BY userId", User.class);
            List<User> users = query.list();
            ss.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

