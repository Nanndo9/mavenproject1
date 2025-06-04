package AlunoApp.src.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory() {
        try {
            // Cria o SessionFactory a partir do hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure(); // Carrega o arquivo hibernate.cfg.xml
            
            System.out.println("Configuração do Hibernate carregada com sucesso!");
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Falha na criação do SessionFactory: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        // Fecha caches e pools de conexão
        getSessionFactory().close();
    }
}