package com.global.hbc.upyun;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

public class UpYunUtils {
	/**
	 * 计算policy
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String getPolicy(Map<String, Object> paramMap){

		JSONObject obj = new JSONObject(paramMap);

		return Base64Coder.encodeString(obj.toString());
	}

	/**
	 * 计算签名
	 * 
	 * 表单API中，"signature" 用于校验返回数据的合法性，按照下面的规则计算生成： 1.
	 * 将参数（"signature"参数不参与计算）键值对根据key的字典顺序排序后，连接成一个字符串； 2.
	 * 将第一步生成的字符串，与"secretKey"连接，计算md5；
	 * 
	 * 上面说的"secretKey"的值有2种情况： --> token_secret(在分块上传第一步初始化请求的返回中获取)
	 * 第一步初始化请求以及最后一步『直接返回』（没有指定return_url和notify_url）的时候使用 --> form_api_secret
	 * 第二步分块请求以及最后一步在指定了return_url或者notify_url的情况下使用
	 * 
	 * ps:通常我们建议签名数据在服务器端生成，仅在手机端需要上传文件的时候，才从服务器端取得签名后的数据，以防止表单API验证密钥泄露出去。
	 * 
	 * @param paramMap
	 * @param secretKey
	 * @return
	 */
	public static String getSignature(Map<String, Object> paramMap,
			String secretKey) {
		Object[] keys = paramMap.keySet().toArray();
		Arrays.sort(keys);

		StringBuffer tmp = new StringBuffer("");
		for (Object key : keys) {
			tmp.append(key).append(paramMap.get(key));
		}
		tmp.append(secretKey);
		return md5Hex(tmp.toString().getBytes());
	}
	
	/**
	 * android 中无法使用DigestUtils.md5Hex(data),这是替代方法
	 * 
	 * @return
	 */
	public static String md5Hex(InputStream stream){
		try {
			return new String(Hex.encodeHex(DigestUtils.md5(stream)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String md5Hex(byte[] data) {
		return new String(Hex.encodeHex(DigestUtils.md5(data)));
	}

	/**
	 * 计算分块数目
	 * 
	 * @param file
	 * @param blockSize
	 * @return
	 */
	public static int getBlockNum(File file, int blockSize)
			throws UpYunException {
		if (blockSize < UploaderManager.MIN_BLOCK_SIZE) {
			throw new UpYunException("BlockSize should be at least " + UploaderManager.MIN_BLOCK_SIZE
					+ ".");
		}
		int blockNum = 0;
		int size = (int) file.length() % blockSize;
		blockNum = (int) file.length() / blockSize;
		if (size != 0) {
			blockNum++;
		}

		return blockNum;
	}
}
