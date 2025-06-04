/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AlunoApp.src.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "alunos")
public class Aluno {
    
    @Id
    @Column(name = "matricula")
    private String matricula;
    
    @Column(name = "nome", length = 100)
    private String nome;
    
    @Column(name = "idade")
    private int idade;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento")
    private Date dataNascimento;
    
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    @Column(name = "cpf", length = 14)
    private String cpf;

    // Formato padrão para exibição da data
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // Getters e Setters
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "Nome: " + nome +
               "\nMatrícula: " + matricula +
               "\nIdade: " + idade +
               "\nData de Nascimento: " + (dataNascimento != null ? sdf.format(dataNascimento) : "N/A") +
               "\nTelefone: " + telefone +
               "\nCPF: " + cpf;
    }
}