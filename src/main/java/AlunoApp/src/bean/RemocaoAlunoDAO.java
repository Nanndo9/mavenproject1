/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AlunoApp.src.bean;

import AlunoApp.src.bean.Aluno;
import java.util.ArrayList;
import java.util.List;

public class RemocaoAlunoDAO implements AlunoDAO {

    @Override
    public List<Aluno> removerAluno(List<Aluno> alunos, Aluno a) {
        if (alunos == null || a == null) {
            return alunos; 
        }
        
        List<Aluno> novaLista = new ArrayList<>();
        
        for (Aluno alunoNaLista : alunos) {
            if (!alunoNaLista.getMatricula().equals(a.getMatricula())) {
                novaLista.add(alunoNaLista);
            }
        }
        
        return novaLista; 
    }
}
