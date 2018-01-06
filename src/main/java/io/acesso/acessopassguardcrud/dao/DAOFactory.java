package io.acesso.acessopassguardcrud.dao;



/**
 * Factory para criação de objetos DAO
 */
public class DAOFactory {

	/**
	 * Caminho da classe de DAO para ser instanciada
	 */
	private static final String SENHA_SERVICO_DAO_CLASSNAME = "io.acesso.acessopassguardcrud.dao.SenhaServicoXMLDAO";
	


	/**
	 * Cria um objeto de SenhaServicoDAO
	 * @return
	 */
	public static SenhaServicoDAO getSenhaServicoDAO() {
		try {
			// Instancia o objeto via Reflection API
			return (SenhaServicoDAO) Class.forName(SENHA_SERVICO_DAO_CLASSNAME).newInstance();

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
