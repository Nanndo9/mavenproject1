
package AlunoApp.src.bean;

import java.util.List;
import java.util.Iterator;

public class RemocaoAlunoDAO implements AlunoDAO {

    @Override
    public List<Aluno> removerAluno(List<Aluno> alunos, Aluno a) {
        if (alunos == null || a == null) {
            return alunos; // Retorna a lista original se algum parâmetro for nulo
        }
        
        // Usar Iterator para remover com segurança da lista original
        Iterator<Aluno> iterator = alunos.iterator();
        while (iterator.hasNext()) {
            Aluno alunoNaLista = iterator.next();
            if (alunoNaLista.getMatricula().equals(a.getMatricula())) {
                iterator.remove(); // Remove diretamente da lista original
                break;
            }
        }
        
        // Retorna a mesma lista, agora modificada
        return alunos;
    }
}
