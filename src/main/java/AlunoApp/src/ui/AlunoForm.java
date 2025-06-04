/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package AlunoApp.src.ui;

import AlunoApp.src.bean.Aluno;
import AlunoApp.src.exception.MatriculaDuplicadaException;
import AlunoApp.src.bean.AlunoDAO;
import AlunoApp.src.bean.RemocaoAlunoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Calendar;

public class AlunoForm extends javax.swing.JFrame {

    // --- Componentes da Interface Gráfica ---  
    private JTextField txtMatricula, txtNome;
    private JTextField txtIdadeCalculadaDisplay;
    private JFormattedTextField txtDataNascimento, txtTelefone, txtCpf;
    private JButton btnAdicionar, btnBuscar, btnRemover, btnVerificarIdades, btnInserirPosicao, btnListarTodos;

    // --- Lista de Alunos e Utilitários ---
    private List<Aluno> listaAlunos = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static final String NOME_ARQUIVO = "ListagemAlunos.txt";

    public AlunoForm() {
        // --- Configurações da Janela Principal ---
        setTitle("Cadastro de Alunos");
        setSize(600, 400);
        setMinimumSize(new Dimension(800, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        sdf.setLenient(false);
        carregarDeCSV();

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createTitledBorder("Dados do Aluno"));

        // Voltamos ao GridBagLayout simples, alinhado à esquerda
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Garante alinhamento à esquerda

        // --- Linha 0: Matrícula ---
        gbc.gridx = 0; // Coluna 0
        gbc.gridy = 0;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Matrícula:"), gbc);

        gbc.gridx = 1; // Coluna 1
        gbc.weightx = 1.0;
        txtMatricula = new JTextField(30);
        panelForm.add(txtMatricula, gbc);

        // --- Linha 1: Nome ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(30);
        panelForm.add(txtNome, gbc);

        // --- Linha 2: Data de Nascimento e Idade ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Data Nascimento:"), gbc);

        // O painel que agrupa data e idade
        JPanel painelDataIdade = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        txtDataNascimento = new JFormattedTextField(criarMascara("##/##/####"));
        txtDataNascimento.setColumns(7);
        txtDataNascimento.addPropertyChangeListener(evt -> atualizarIdadeCalculada());

        txtIdadeCalculadaDisplay = new JTextField(5);
        txtIdadeCalculadaDisplay.setEditable(false);
        txtIdadeCalculadaDisplay.setFont(new Font("Monospaced", Font.BOLD, 12));
        txtIdadeCalculadaDisplay.setBackground(UIManager.getColor("Label.background"));

        painelDataIdade.add(txtDataNascimento);

        // <<< MUDANÇA PRINCIPAL AQUI >>>
        // Adiciona um espaçador invisível para empurrar a idade para a direita
        painelDataIdade.add(Box.createHorizontalStrut(20)); // Altere 20 para o espaço que desejar

        painelDataIdade.add(new JLabel("Idade:"));
        painelDataIdade.add(txtIdadeCalculadaDisplay);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelForm.add(painelDataIdade, gbc);

        // --- Linha 3: Telefone ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTelefone = new JFormattedTextField(criarMascara("(##) #####-####"));
        txtTelefone.setColumns(15);
        panelForm.add(txtTelefone, gbc);

        // --- Linha 4: CPF ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panelForm.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCpf = new JFormattedTextField(criarMascara("###.###.###-##"));
        txtCpf.setColumns(15);
        panelForm.add(txtCpf, gbc);

        // Espaçador vertical para empurrar tudo para cima
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        panelForm.add(new JPanel(), gbc);

// Adiciona o painel pronto ao Norte do Frame
        add(panelForm, BorderLayout.NORTH);

        // --- Painel de Botões ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar");
        btnBuscar = new JButton("Pesquisar Aluno");
        btnRemover = new JButton("Remover Aluno");
        btnVerificarIdades = new JButton("Mais Novo/Velho");
        btnInserirPosicao = new JButton("Inserir em Posição");
        btnListarTodos = new JButton("Listar Todos");

        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnBuscar);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnVerificarIdades);
        panelBotoes.add(btnInserirPosicao);
        panelBotoes.add(btnListarTodos);
        add(panelBotoes, BorderLayout.SOUTH);

        // --- Adicionando Ações aos Botões ---
        btnAdicionar.addActionListener(this::adicionarAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnRemover.addActionListener(this::removerAluno);
        btnVerificarIdades.addActionListener(this::verificarIdades);
        btnInserirPosicao.addActionListener(this::inserirEmPosicao);
        btnListarTodos.addActionListener(e -> {
            if (listaAlunos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A lista de alunos está vazia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            TabelaAlunosDialog dialogTabela = new TabelaAlunosDialog(this, listaAlunos);
            dialogTabela.setVisible(true);
        });
    }

    private MaskFormatter criarMascara(String formato) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(formato);
            formatter.setPlaceholderCharacter('_'); // Caractere que aparece nos espaços vazios
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar a máscara de formatação: " + e.getMessage(), "Erro Interno", JOptionPane.ERROR_MESSAGE);
        }
        return formatter;
    }

    private int calcularIdade(Date dataNascimento) {
        if (dataNascimento == null) {
            return 0;
        }
        Calendar dataNasc = Calendar.getInstance();
        dataNasc.setTime(dataNascimento);
        Calendar hoje = Calendar.getInstance();

        int idade = hoje.get(Calendar.YEAR) - dataNasc.get(Calendar.YEAR);
        if (hoje.get(Calendar.MONTH) < dataNasc.get(Calendar.MONTH)
                || (hoje.get(Calendar.MONTH) == dataNasc.get(Calendar.MONTH) && hoje.get(Calendar.DAY_OF_MONTH) < dataNasc.get(Calendar.DAY_OF_MONTH))) {
            idade--;
        }
        return idade < 0 ? 0 : idade;
    }

    // NOVO: Método para atualizar o JLabel da idade calculada
    private void atualizarIdadeCalculada() {
        try {
            String dataNascStr = txtDataNascimento.getText();

            // AQUI ESTÁ A CORREÇÃO: trocamos .contains("") por .contains("_")
            if (dataNascStr != null && !dataNascStr.contains("_") && dataNascStr.length() == 10) {
                Date dataNascimento = sdf.parse(dataNascStr);
                int idade = calcularIdade(dataNascimento);
                txtIdadeCalculadaDisplay.setText(String.valueOf(idade));
            } else {
                // Se a data não estiver completa, limpa o campo de idade
                txtIdadeCalculadaDisplay.setText("");
            }
        } catch (ParseException ex) {
            // Se a data for inválida (ex: 30/02/2020), limpa também
            txtIdadeCalculadaDisplay.setText("");
        }
    }

    private void adicionarAluno(ActionEvent e) {
        try {
            if (txtMatricula.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo matrícula é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            verificarMatriculaDuplicada(txtMatricula.getText());
            Aluno novoAluno = criarAlunoPeloFormulario();
            listaAlunos.add(novoAluno);

            // ALTERADO: Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Aluno " + novoAluno.getNome() + " adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            salvarParaCSV();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarAluno(ActionEvent e) {
        String matricula = JOptionPane.showInputDialog(this, "Digite a matrícula do aluno a ser buscado:");
        if (matricula == null || matricula.trim().isEmpty()) {
            return;
        }

        for (Aluno aluno : listaAlunos) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula.trim())) {
                // ALTERADO: Exibe o resultado na tabela
                List<Aluno> resultadoBusca = new ArrayList<>();
                resultadoBusca.add(aluno); // Adiciona apenas o aluno encontrado à lista

                TabelaAlunosDialog dialog = new TabelaAlunosDialog(this, resultadoBusca);
                dialog.setVisible(true);
                return; // Encerra o método após encontrar
            }
        }

        // Se o loop terminar, o aluno não foi encontrado
        JOptionPane.showMessageDialog(this, "Aluno com matrícula '" + matricula + "' não encontrado.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
    }

    private void removerAluno(ActionEvent e) {
        String matricula = JOptionPane.showInputDialog(this, "Digite a matrícula do aluno a ser removido:");
        if (matricula == null || matricula.trim().isEmpty()) {
            return;
        }

        // 1. Encontrar o objeto Aluno que corresponde à matrícula
        Aluno alunoParaRemover = null;
        for (Aluno aluno : listaAlunos) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula.trim())) {
                alunoParaRemover = aluno;
                break; // Encontrou o aluno, pode parar o loop
            }
        }

        // 2. Verificar se o aluno foi encontrado
        if (alunoParaRemover != null) {
            // 3. Instanciar e usar o DAO para remover o aluno
            AlunoDAO dao = new RemocaoAlunoDAO();

            // 4. A lista principal da nossa classe é ATUALIZADA com a nova lista retornada pelo DAO
            this.listaAlunos = dao.removerAluno(this.listaAlunos, alunoParaRemover);

            // 5. Salvar a nova lista no arquivo CSV
            salvarParaCSV();

            // 6. Mostrar mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Aluno " + alunoParaRemover.getNome() + " removido com sucesso!", "Remoção Concluída", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // Mensagem caso o aluno não seja encontrado
            JOptionPane.showMessageDialog(this, "Aluno com matrícula '" + matricula + "' não encontrado para remoção.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verificarIdades(ActionEvent e) {
        if (listaAlunos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A lista de alunos está vazia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Aluno maisNovo = listaAlunos.get(0);
        Aluno maisVelho = listaAlunos.get(0);

        for (Aluno aluno : listaAlunos) {
            if (aluno.getDataNascimento().after(maisNovo.getDataNascimento())) {
                maisNovo = aluno;
            }
            if (aluno.getDataNascimento().before(maisVelho.getDataNascimento())) {
                maisVelho = aluno;
            }
        }

        // ALTERADO: Exibe o resultado na tabela
        List<Aluno> resultadoIdade = new ArrayList<>();
        resultadoIdade.add(maisVelho);
        // Adiciona o mais novo apenas se for uma pessoa diferente (para listas com > 1 aluno)
        if (!maisNovo.getMatricula().equals(maisVelho.getMatricula())) {
            resultadoIdade.add(maisNovo);
        }

        TabelaAlunosDialog dialog = new TabelaAlunosDialog(this, resultadoIdade);
        dialog.setVisible(true);
    }

    private void inserirEmPosicao(ActionEvent e) {
        try {
            if (txtMatricula.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os dados do aluno para inserir.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String posStr = JOptionPane.showInputDialog(this, "Digite a posição para inserir (ex: 1, 2, 3...):", "Inserir em Posição", JOptionPane.QUESTION_MESSAGE);
            if (posStr == null) {
                return;
            }

            int posicao = Integer.parseInt(posStr);
            if (posicao < 1 || posicao > listaAlunos.size() + 1) {
                JOptionPane.showMessageDialog(this, "Posição inválida. Deve ser entre 1 e " + (listaAlunos.size() + 1), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            verificarMatriculaDuplicada(txtMatricula.getText());
            Aluno novoAluno = criarAlunoPeloFormulario();
            listaAlunos.add(posicao - 1, novoAluno);

            // ALTERADO: Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Aluno " + novoAluno.getNome() + " inserido com sucesso na posição " + posicao + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            salvarParaCSV();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtMatricula.setText("");
        txtNome.setText("");
        txtIdadeCalculadaDisplay.setText("");
        txtDataNascimento.setValue(null);
        txtTelefone.setValue(null);
        txtCpf.setValue(null);
    }

    private Aluno criarAlunoPeloFormulario() throws ParseException, NumberFormatException {
        Aluno aluno = new Aluno();
        aluno.setMatricula(txtMatricula.getText().trim());
        aluno.setNome(txtNome.getText().trim());

        Date dataNasc = null;
        String dataNascStr = txtDataNascimento.getText();
        if (dataNascStr != null && !dataNascStr.contains("_") && dataNascStr.length() == 10) {
            dataNasc = sdf.parse(dataNascStr); // sdf.setLenient(false) já está no construtor
            aluno.setDataNascimento(dataNasc);
            aluno.setIdade(calcularIdade(dataNasc)); // MODIFICAÇÃO: Calcula e define a idade
        } else {
            // Esta exceção deve ser tratada no método que chama criarAlunoPeloFormulario (adicionarAluno)
            throw new ParseException("Data de nascimento incompleta ou inválida.", 0);
        }

        aluno.setTelefone(txtTelefone.getText()); // JFormattedTextField retorna o valor com máscara
        aluno.setCpf(txtCpf.getText());
        return aluno;
    }

    private void verificarMatriculaDuplicada(String matricula) throws MatriculaDuplicadaException {
        for (Aluno a : listaAlunos) {
            if (a.getMatricula().equalsIgnoreCase(matricula)) {
                throw new MatriculaDuplicadaException("Erro: A matrícula '" + matricula + "' já existe!");
            }
        }
    }

    private void salvarParaCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO))) {
            for (Aluno aluno : listaAlunos) {
                String dataFormatada = sdf.format(aluno.getDataNascimento());
                String linhaCSV = String.join(";",
                        aluno.getMatricula(),
                        aluno.getNome(),
                        String.valueOf(aluno.getIdade()),
                        dataFormatada,
                        aluno.getTelefone(),
                        aluno.getCpf()
                );
                writer.println(linhaCSV);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDeCSV() {
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";", -1);
                if (dados.length == 6) {
                    Aluno aluno = new Aluno();
                    aluno.setMatricula(dados[0]);
                    aluno.setNome(dados[1]);

                    // MODIFICAÇÃO: Calcula a idade com base na data de nascimento carregada
                    Date dataNascCarregada = null;
                    if (!dados[3].trim().isEmpty()) {
                        try {
                            dataNascCarregada = sdf.parse(dados[3].trim());
                            aluno.setDataNascimento(dataNascCarregada);
                            aluno.setIdade(calcularIdade(dataNascCarregada));
                        } catch (ParseException pe) {
                            System.err.println("Erro ao parsear data do CSV para aluno " + dados[0] + ": " + dados[3]);
                            // Se a data for inválida, tenta usar a idade do CSV como fallback
                            try {
                                aluno.setIdade(Integer.parseInt(dados[2].trim()));
                            } catch (NumberFormatException nfe) {
                                aluno.setIdade(0); // Ou algum valor padrão
                            }
                        }
                    } else {
                        // Se não há data de nascimento no CSV, tenta usar a idade do CSV
                        try {
                            aluno.setIdade(Integer.parseInt(dados[2].trim()));
                        } catch (NumberFormatException nfe) {
                            aluno.setIdade(0); // Ou algum valor padrão
                        }
                    }

                    aluno.setTelefone(dados[4]);
                    aluno.setCpf(dados[5]);
                    listaAlunos.add(aluno);
                }
            }
        } catch (IOException | NumberFormatException e) { // ParseException é tratada dentro do loop
            JOptionPane.showMessageDialog(this, "Erro ao carregar o arquivo CSV: " + e.getMessage() + "\nVerifique o formato do arquivo.", "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AlunoForm().setVisible(true);
        });
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 774, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AlunoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AlunoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AlunoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AlunoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlunoForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
