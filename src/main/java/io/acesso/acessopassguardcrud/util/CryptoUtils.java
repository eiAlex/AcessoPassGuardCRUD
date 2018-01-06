package io.acesso.acessopassguardcrud.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilitários para  gerar criptografia.
 */
public class CryptoUtils {

	/**
	 * Criptografa AES.
	 * @param keyBytes Bytes da chave
	 * @param dataBytes Bytes dos dados
	 * @return Bytes criptografados
	 * @throws Exception
	 */
	public static byte[] encryptAES(byte[] keyBytes, byte[] dataBytes) throws Exception {
		return handleAES(keyBytes, dataBytes, Cipher.ENCRYPT_MODE);
	}

	/**
	 * Descriptografa dados usando o algoritmo AES.
	 * @param keyBytes Bytes da chave
	 * @param dataBytes Bytes dos dados
	 * @return Bytes descriptografados
	 * @throws Exception
	 */
	public static byte[] decryptAES(byte[] keyBytes, byte[] dataBytes) throws Exception {
		return handleAES(keyBytes, dataBytes, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * @param keyBytes Bytes da chave
	 * @param dataBytes Bytes dos dados
	 * @param mode Modo: criptografia ou descriptografia. 
	 * @return Bytes criptografados ou descriptografados
	 * @throws Exception
	 */
	private static byte[] handleAES(byte[] keyBytes, byte[] dataBytes, int mode) throws Exception {
		// Verifica validade da chave
		if (keyBytes == null || keyBytes.length != 16) {
			throw new Exception("A chave é inválida");
		}
		
		// Verifica validade dos dados
		if (dataBytes == null) {
			throw new Exception("Não existem dados");
		}
		
		// Cria um objeto que representa a chave AES
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		
		// Instancia o algoritmo AES
		Cipher cipher = Cipher.getInstance("AES");
		
		// Inicializa o algoritmo
		cipher.init(mode, key);
		
		// Executa o processo de criptografia ou descriptografia
		return cipher.doFinal(dataBytes);
	}
}
