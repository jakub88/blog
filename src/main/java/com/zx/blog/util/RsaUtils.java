package com.zx.blog.util;

import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtils {
	/**
	 * 从文件中读取公钥
	 *
	 * @param filename 公钥保存路径，相对于classpath
	 * @return 公钥对象
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String filename) throws Exception {
		byte[] bytes = readFile(filename);
		return getPublicKey(bytes);
	}

	/**
	 * 从文件中读取密钥
	 *
	 * @param filename 私钥保存路径，相对于classpath
	 * @return 私钥对象
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String filename) throws Exception {
		byte[] bytes = readFile(filename);
		return getPrivateKey(bytes);
	}

	/**
	 * 获取公钥
	 *
	 * @param bytes 公钥的字节形式
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(byte[] bytes) throws Exception {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePublic(spec);
	}

	/**
	 * 获取密钥
	 *
	 * @param bytes 私钥的字节形式
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(byte[] bytes) throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}

	/**
	 * 根据密文，生存rsa公钥和私钥,并写入指定文件
	 *
	 * @param publicKeyFilename  公钥文件路径
	 * @param privateKeyFilename 私钥文件路径
	 * @param secret             生成密钥的密文
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void generateKey(String publicKeyFilename, String privateKeyFilename, String secret) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		SecureRandom secureRandom = new SecureRandom(secret.getBytes());
		keyPairGenerator.initialize(2048, secureRandom);
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// 获取公钥并写出
		byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
		writeFile(publicKeyFilename, publicKeyBytes);
		// 获取私钥并写出
		byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
		writeFile(privateKeyFilename, privateKeyBytes);
	}

	private static byte[] readFile(String fileName) throws Exception {
		InputStream is = ResourceUtils.class.getClassLoader().getResourceAsStream(fileName);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if (is == null) {
			return null;
		}
		byte[] buffer = new byte[1024];
		int n = 0;
		while (-1 != (n = is.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}

	private static void writeFile(String destPath, byte[] bytes) throws IOException {
		File dest = new File(destPath);
		if (!dest.exists()) {
			try {
				dest.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Files.write(dest.toPath(), bytes);
	}

}