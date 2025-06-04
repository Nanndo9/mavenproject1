/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AlunoApp.src.ui;

/**
 *
 * @author MAYARA
 */

import AlunoApp.src.bean.Aluno;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.List;

public class TabelaAlunosDialog extends JDialog {

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TabelaAlunosDialog(Frame owner, List<Aluno> alunos) {
        // O 'true' no final torna a janela "modal", ou seja, ela bloqueia a janela principal
        super(owner, "Lista de Alunos", true);

        // 1. Definir os nomes das colunas
        String[] colunas = {"Matrícula", "Nome", "Idade", "Nascimento", "Telefone", "CPF"};

        // 2. Criar o modelo da tabela sem nenhuma linha inicial
        modeloTabela = new DefaultTableModel(colunas, 0);

        // 3. Criar a JTable com o modelo
        tabela = new JTable(modeloTabela);

        // 4. Preencher a tabela com os dados dos alunos
        preencherTabela(alunos);
        
        // 5. Colocar a tabela dentro de um painel com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(tabela);
        
        // 6. Adicionar o painel de rolagem à janela de diálogo
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // 7. Configurações finais da janela
        setSize(750, 400); // Tamanho da janela da tabela
        setLocationRelativeTo(owner); // Centralizar em relação à janela principal
    }

    private void preencherTabela(List<Aluno> alunos) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        // Limpa quaisquer dados antigos da tabela
        modeloTabela.setRowCount(0);

        // Para cada aluno na lista, cria uma linha de dados (Object[]) e adiciona ao modelo
        for (Aluno aluno : alunos) {
            Object[] linha = {
                aluno.getMatricula(),
                aluno.getNome(),
                aluno.getIdade(),
                sdf.format(aluno.getDataNascimento()),
                aluno.getTelefone(),
                aluno.getCpf()
            };
            modeloTabela.addRow(linha);
        }
    }
}