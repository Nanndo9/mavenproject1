/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AlunoApp.src.bean;

import AlunoApp.src.bean.Aluno;
import java.util.List;

public interface AlunoDAO {
    /**
     * Recebe uma lista de alunos e um aluno para remover.
     * @param alunos A lista original de alunos.
     * @param a O objeto Aluno a ser removido.
     * @return Uma nova lista contendo os alunos, exceto o que foi removido.
     */
    public List<Aluno> removerAluno(List<Aluno> alunos, Aluno a);
}
