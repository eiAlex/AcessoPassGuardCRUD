package io.acesso.acessopassguardcrud.dao;



import java.util.List;

import io.acesso.acessopassguardcrud.model.SenhaServico;

/**
 * DAO relacionado aos objetos SenhaServico
 */
public interface SenhaServicoDAO {

	/**
	 * Carrega todas as senhas cadastradas
	 * @return
	 */
	public List<SenhaServico> load();

	/**
	 * Grava as senhas
	 * @param senhasServico Lista de senhas
	 */
	public void store(List<SenhaServico> senhasServico);
	
	/**
	 * Carrega as senhas com base em um filtro
	 * @param text Texto do filtro
	 * @return Lista de senhas
	 */
	public List<SenhaServico> filter(String text);
	
	/**
	 * Gera um novo ID para uma senha
	 * @return Novo ID
	 */
	public int generateId();
}
