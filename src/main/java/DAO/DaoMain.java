package DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.io.File;

public class DaoMain{
    public static void loadIntoDB (String tableName, File csvFile) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        tableName = tableName.toUpperCase();
        try {
            Transaction transaction = session.beginTransaction();
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME LIKE '" + tableName + "' ";
            Query query = session.createSQLQuery(sql);
            if (query.list().size() != 0) {
                sql = "DROP TABLE " + tableName;
                query = session.createSQLQuery(sql);
                query.executeUpdate();
            }
            sql = "CREATE TABLE " + tableName + " AS SELECT * FROM CSVREAD ('" + csvFile.getAbsolutePath() + "')";
            query = session.createSQLQuery(sql);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }
}
