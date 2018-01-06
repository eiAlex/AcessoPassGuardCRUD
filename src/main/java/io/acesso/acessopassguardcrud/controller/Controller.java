package io.acesso.acessopassguardcrud.controller;

import java.util.List;

import io.acesso.acessopassguardcrud.dao.DAOFactory;
import io.acesso.acessopassguardcrud.dao.SenhaServicoDAO;
import io.acesso.acessopassguardcrud.model.SenhaServico;
import io.acesso.acessopassguardcrud.util.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

	@FXML
	private TableView<SenhaServico> table;
	@FXML
	private TextField txtServico;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtSenha;
	@FXML
	private TextArea txtObservacoes;
	@FXML
	private Button btnNovo;
	@FXML
	private Button btnEditar;
	@FXML
	private Button btnExcluir;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnConfirm;
	@FXML
	private TextField txtSearch;
	@FXML
	private Button btnSearch;
	@FXML
	private Button btnClearSearch;

	/**
	 * Indica se o registro está em modo para edição.
	 */
	private BooleanProperty editMode = new SimpleBooleanProperty();
	
	/**
	 * Edita se os resultados da tabela estão filtrados
	 */
	private BooleanProperty resultsFiltered = new SimpleBooleanProperty();

	/**
	 * Armazena o registro sendo altera ou inserido
	 */
	private SenhaServico currentSenhaServico;

	/**
	 * DAO para manipular os dados
	 */
	private SenhaServicoDAO dao;

	/**
	 * Método chamado pelo JavaFX quando o controller é inicializado
	 */
	@FXML
	private void initialize() {
		dao = DAOFactory.getSenhaServicoDAO();
		loadData(false);

		// Adiciona um listener para saber quando um novo item é selecionado na tabela
		table.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			unbindData(oldValue);
			bindData(newValue);
		});

		// Faz o binding das properties
		
		btnNovo.disableProperty().bind(editMode);
		btnEditar.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull().or(editMode));
		btnExcluir.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull().or(editMode));
		btnCancel.disableProperty().bind(editMode.not());
		btnConfirm.disableProperty().bind(editMode.not());
		btnSearch.disableProperty().bind(txtSearch.textProperty().isEmpty());
		btnClearSearch.disableProperty().bind(resultsFiltered.not());

		txtServico.disableProperty().bind(editMode.not());
		txtLogin.disableProperty().bind(editMode.not());
		txtSenha.disableProperty().bind(editMode.not());
		txtObservacoes.disableProperty().bind(editMode.not());

		table.disableProperty().bind(editMode);
	}

	/**
	 * Sai da aplicação =/.
	 */
	@FXML
	public void exit() {
		Platform.exit();
	}

	/**
	 * Faz o binding dos campos do formulário com as propriedades de SenhaServico
	 * @param senhaServico
	 */
	private void bindData(SenhaServico senhaServico) {
		if (senhaServico != null) {
			txtServico.textProperty().bindBidirectional(senhaServico.servicoProperty());
			txtLogin.textProperty().bindBidirectional(senhaServico.loginProperty());
			txtSenha.textProperty().bindBidirectional(senhaServico.senhaProperty());
			txtObservacoes.textProperty().bindBidirectional(senhaServico.observacoesProperty());
		}
	}

	/**
	 * Desfaz o binding dos campos do formulário com as propriedades de SenhaServico
	 * @param senhaServico
	 */
	private void unbindData(SenhaServico senhaServico) {
		if (senhaServico != null) {
			txtServico.textProperty().unbindBidirectional(senhaServico.servicoProperty());
			txtLogin.textProperty().unbindBidirectional(senhaServico.loginProperty());
			txtSenha.textProperty().unbindBidirectional(senhaServico.senhaProperty());
			txtObservacoes.textProperty().unbindBidirectional(senhaServico.observacoesProperty());

			clearForm();
		}
	}

	/**
	 * Cria um novo registro
	 */
	@FXML
	public void onNew() {
		table.getSelectionModel().clearSelection();
		editMode.set(true);
		currentSenhaServico = new SenhaServico();
		bindData(currentSenhaServico);
		txtServico.requestFocus();
	}

	/**
	 * Altera um registro existente
	 */
	@FXML
	public void onEdit() {
		editMode.set(true);
		currentSenhaServico = table.getSelectionModel().getSelectedItem();
	}

	/**
	 * Exclui um registro
	 */
	@FXML
	public void onDelete() {
		table.getItems().removeIf(table.getSelectionModel().getSelectedItem()::equals);
		storeData();
	}

	/**
	 * Cancela a edição
	 */
	@FXML
	public void onCancel() {
		editMode.set(false);

		if (currentSenhaServico.getId() == 0) {
			unbindData(currentSenhaServico);
			clearForm();
		}
	}

	/**
	 * Confirma a inserção/alteração de um registro
	 */
	@FXML
	public void onConfirm() {
		String errorMessage = validateForm();
		if (!errorMessage.isEmpty()) {
			showValidationError(errorMessage);
			return;
		}

		editMode.set(false);

		if (currentSenhaServico.getId() == 0) {
			currentSenhaServico.setId(dao.generateId());
			table.getItems().add(currentSenhaServico);
			unbindData(currentSenhaServico);
			clearForm();
			table.getSelectionModel().select(currentSenhaServico);
		}

		storeData();
	}

	/**
	 * Produra por registros
	 */
	@FXML
	public void onSearch() {
		loadData(true);
		resultsFiltered.set(true);
	}
	
	/**
	 * Limpa a pesquisa
	 */
	@FXML
	public void clearSearch() {
		loadData(false);
		txtSearch.clear();
		resultsFiltered.set(false);
	}

	/**
	 * Limpa os campos do formulário
	 */
	private void clearForm() {
		txtServico.clear();
		txtLogin.clear();
		txtSenha.clear();
		txtObservacoes.clear();
	}

	/**
	 * Valida os dados do formulário
	 * @return true se os dados são válidos; false, caso contrário
	 */
	private String validateForm() {
		StringBuilder errorMessage = new StringBuilder();

		if (StringUtils.isEmpty(currentSenhaServico.getServico())) {
			errorMessage.append("Preencha o site/serviço").append(StringUtils.newLine());
		}

		if (StringUtils.isEmpty(currentSenhaServico.getLogin())) {
			errorMessage.append("Preencha o login").append(StringUtils.newLine());
		}

		if (StringUtils.isEmpty(currentSenhaServico.getSenha())) {
			errorMessage.append("Preencha a senha").append(StringUtils.newLine());
		}

		return errorMessage.toString();
	}

	/**
	 * Exibe um dialog de alerta de validação
	 * @param message Mensagem a ser mostrada
	 */
	private void showValidationError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erro de validação");
		alert.setHeaderText("Informação incorreta");
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	/**
	 * Carrega os dados usando o DAO
	 * @param filter
	 */
	private void loadData(boolean filter) {
		List<SenhaServico> itens;
		if (!filter) {
			itens = dao.load();
		} else {
			itens = dao.filter(txtSearch.getText());
		}
		
		table.setItems(FXCollections.observableArrayList(itens));
	}
	
	/**
	 * Grava os dados usando o DAO
	 */
	private void storeData() {
		dao.store(table.getItems());
	}
}
