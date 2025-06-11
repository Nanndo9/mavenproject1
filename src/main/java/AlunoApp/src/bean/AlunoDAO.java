/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AlunoApp.src.bean;

import AlunoApp.src.bean.Aluno;
import java.util.List;

public interface AlunoDAO { 
    /**
     * Remove um aluno da lista de alunos fornecida.
     * A lista original é modificada.
     * @param alunos A lista original de alunos da qual o aluno será removido.
     * @param a O objeto Aluno a ser removido.
     * @return A mesma lista de alunos, agora modificada (ou a lista original se a remoção falhar ou os parâmetros forem nulos).
     */
    public List<Aluno> removerAluno(List<Aluno> alunos, Aluno a);
}
