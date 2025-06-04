package AlunoApp.src.bean;

import AlunoApp.src.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class AlunoDAOHibernate {
    
    public void salvar(Aluno aluno) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(aluno);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
    
    public void atualizar(Aluno aluno) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(aluno);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
    
    public Aluno buscarPorMatricula(String matricula) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Aluno.class, matricula);
        }
    }
    
    public List<Aluno> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Aluno", Aluno.class).list();
        }
    }
    
    public void excluir(Aluno aluno) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Se o objeto não estiver no contexto de persistência, primeiro precisamos buscá-lo
            if (!session.contains(aluno)) {
                aluno = session.get(Aluno.class, aluno.getMatricula());
                if (aluno == null) {
                    return; // Aluno não existe no banco
                }
            }
            
            session.remove(aluno);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}