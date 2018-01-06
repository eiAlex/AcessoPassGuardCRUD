package io.acesso.acessopassguardcrud.dao;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.acesso.acessopassguardcrud.model.SenhaServico;
import io.acesso.acessopassguardcrud.util.CryptoUtils;

/**
 * DAO de SenhaServico que armazena os dados em um documento XML
 **/
public class SenhaServicoXMLDAO implements SenhaServicoDAO {
	
	/**
	 * Chave secreta para usar a criptografia da senha no XML
	 **/
	private static final byte[] SECRET_KEY = "LDJGOGDLKJFSDYFK".getBytes();
	
	/**
	 * Local do arquivo XML
	 */
	private static final Path STORAGE_FILE = Paths.get(System.getProperty("user.home"), "senhas.xml");

	/**
	 * @see io.acesso.acessopassguardcrud.SenhaServicoDAO#load()
	 */
	@Override
	public List<SenhaServico> load() {
		List<SenhaServico> senhasServico = new ArrayList<>();
		
		try {
			if (!Files.exists(STORAGE_FILE)) {
				return senhasServico;
			}
			
			// OS dados do XML e armazena em uma lista de SenhaServico
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbFactory.newDocumentBuilder();
			Document doc = db.parse(Files.newInputStream(STORAGE_FILE));
			
			NodeList senhaServicoTags = doc.getElementsByTagName("SenhaServico");
			
			for (int i = 0; i < senhaServicoTags.getLength(); i++) {
				Element senhaServicoTag = (Element) senhaServicoTags.item(i);
				SenhaServico senhaServico = new SenhaServico();
				senhaServico.setId(Integer.parseInt(senhaServicoTag.getAttribute("id")));
				senhaServico.setServico(senhaServicoTag.getElementsByTagName("Servico").item(0).getTextContent());
				senhaServico.setLogin(senhaServicoTag.getElementsByTagName("Login").item(0).getTextContent());
				senhaServico.setSenha(decrypt(senhaServicoTag.getElementsByTagName("Senha").item(0).getTextContent()));
				senhaServico.setObservacoes(senhaServicoTag.getElementsByTagName("Observacoes").item(0).getTextContent());
				senhasServico.add(senhaServico);
			}
			
			return senhasServico;
		
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * @see io.acesso.acessopassguardcrud.SenhaServicoDAO#store(java.util.List)
	 */
	@Override
	public void store(List<SenhaServico> senhasServico) {
		try {
			// monta uma árvore DOM na memória e armazena os dados no documetno XML.
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbFactory.newDocumentBuilder();

			Document doc = db.newDocument();
			
			Element rootTag = doc.createElement("AcessoPassGuardCRUD");
			doc.appendChild(rootTag);
			
			// Itera sobre cada item da lista
			senhasServico.forEach(senhaServico -> {
				Element senhaServicoTag = doc.createElement("SenhaServico");
				senhaServicoTag.setAttribute("id", String.valueOf(senhaServico.getId()));
				
				Element servicoTag = doc.createElement("Servico");
				servicoTag.appendChild(doc.createTextNode(senhaServico.getServico()));
				senhaServicoTag.appendChild(servicoTag);
				
				Element loginTag = doc.createElement("Login");
				loginTag.appendChild(doc.createTextNode(senhaServico.getLogin()));
				senhaServicoTag.appendChild(loginTag);
				
				Element senhaTag = doc.createElement("Senha");
				senhaTag.appendChild(doc.createTextNode(encrypt(senhaServico.getSenha())));
				senhaServicoTag.appendChild(senhaTag);
				
				Element observacoes = doc.createElement("Observacoes");
				observacoes.appendChild(doc.createCDATASection(senhaServico.getObservacoes() == null ? "" : senhaServico.getObservacoes()));
				senhaServicoTag.appendChild(observacoes);
				
				rootTag.appendChild(senhaServicoTag);
			});
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(Files.newOutputStream(STORAGE_FILE));
			t.transform(source, result);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * @see io.acesso.acessopassguardcrud.dao.SenhaServicoDAO#filter(java.lang.String)
	 */
	@Override
	public List<SenhaServico> filter(String text) {
		List<SenhaServico> itens = load();
		String textUpper = text.toUpperCase();
		
		// Filtra os itens da coleção com base no filtro
		return itens.stream()
			.filter(s -> s.getLogin().toUpperCase().contains(textUpper) || s.getServico().toUpperCase().contains(text))
			.collect(Collectors.toList());
	}
	
	/**
	 * @see io.acesso.acessopassguardcrud.dao.SenhaServicoDAO#generateId()
	 */
	@Override
	public int generateId() {
		return load()
				.stream()
				.mapToInt(e -> e.getId() + 1)
				.max()
				.orElse(1);
	}
	
	/**
	 * Criptografa a senha
	 * @param senha Senha a ser criptografada
	 * @return Senha criptografada
	 */
	private String encrypt(String senha) {
		try {
			// Usa a codificação Base64 para poder gravar os bytes em um arquivo texto
			byte[] encBytes = CryptoUtils.encryptAES(SECRET_KEY, senha.getBytes());
			byte[] base64Bytes = Base64.getEncoder().encode(encBytes);
			return new String(base64Bytes);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Descriptografa a senha
	 * @param senha Senha a ser descriptografada
	 * @return Senha descriptografada
	 */
	private String decrypt(String senha) {
		try {
			// A senha está codificada no padrão Base64
			byte[] base64Bytes = senha.getBytes();
			byte[] encBytes = Base64.getDecoder().decode(base64Bytes);
			byte[] decBytes = CryptoUtils.decryptAES(SECRET_KEY, encBytes);
			return new String(decBytes);
		
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
