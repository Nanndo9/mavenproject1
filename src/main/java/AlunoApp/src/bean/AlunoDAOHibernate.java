package AlunoApp.src.bean;

import AlunoApp.src.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AlunoDAOHibernate {
    
    public void salvar(Aluno aluno) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(aluno);
            transaction.commit();
            System.out.println("Aluno salvo no banco de dados com sucesso: " + aluno.getMatricula());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Erro ao salvar aluno: " + e.getMessage());
            throw e;
        }
    }

    public void atualizar(Aluno aluno) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(aluno);
            transaction.commit();
            System.out.println("Aluno atualizado no banco de dados: " + aluno.getMatricula());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Erro ao atualizar aluno: " + e.getMessage());
            throw e;
        }
    }
}