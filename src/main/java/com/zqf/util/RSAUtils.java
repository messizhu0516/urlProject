package com.zqf.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * 
 * @author IceWee
 * @date 2012-4-26
 * @version 1.0
 */
public class RSAUtils {
	public static final String KEY_ALGORTHM = "RSA";//
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	public static final String PUBLIC_KEY = "RSAPublicKey";// 公钥
	public static final String PRIVATE_KEY = "RSAPrivateKey";// 私钥

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		// 解密公钥
		byte[] keyBytes = Coder.decryptBASE64(publicKey);
		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		// 指定加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		// 取公钥匙对象
		PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicKey2);
		signature.update(data);
		// 验证签名是否正常
		return signature.verify(Coder.decryptBASE64(sign));

	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            //加密数据
	 * @param privateKey
	 *            //私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		// 解密私钥
		byte[] keyBytes = Coder.decryptBASE64(privateKey);
		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		// 指定加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		// 取私钥匙对象
		PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateKey2);
		signature.update(data);

		return Coder.encryptBASE64(signature.sign());
	}

	/**
	 * 用公钥解密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
		// 对私钥解密
		byte[] keyBytes = Coder.decryptBASE64(key);
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用公钥加密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
		// 对公钥解密
		byte[] keyBytes = Coder.decryptBASE64(key);
		// 取公钥
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}
	
	/**
	 * BCD转字符串
	 */
	public static String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}
	
	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	public static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	/**
	 * 用私钥解密 * @param data 加密数据
	 * 
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
		// 对私钥解密
		byte[] keyBytes = Coder.decryptBASE64(key);

		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用私钥加密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
		// 解密密钥
		byte[] keyBytes = Coder.decryptBASE64(key);
		// 取私钥
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 取得公钥，并转化为String类型
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Coder.encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得私钥，并转化为String类型
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Coder.encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORTHM);
		keyPairGenerator.initialize(1024);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);

		return keyMap;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> keyMap = RSAUtils.initKey();
		String privateKey = RSAUtils.getPrivateKey(keyMap);
		System.out.println(privateKey);
		String publicKey = RSAUtils.getPublicKey(keyMap);
		System.out.println(publicKey);
	}
	
}
