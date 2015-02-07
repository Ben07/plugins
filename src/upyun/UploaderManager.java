package com.global.hbc.upyun;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploaderManager {
	private static UploaderManager instance = null;
	private HttpManager httpManager = new HttpManager();
	public static final int MIN_BLOCK_SIZE = 500 * 1024;// 500K
	
	private String host = "http://m0.api.upyun.com/";
	private String bucket;
	private int blockSize = 500 * 1024;
	private long expiration = Calendar.getInstance().getTimeInMillis() + 60*1000; // 60s
	
	private UploaderManager(String bucket) {
		this.bucket = bucket;
	}
	
	public static UploaderManager getInstance(String bucket) {
		if (instance == null || !bucket.equals(instance.getBucket())) {
			instance = new UploaderManager(bucket);
			return instance;
		}
		return instance;
	}

	public void upload(String policy, String signature, File localFile,
			ProgressListener progressListener, CompleteListener completeListener)
			throws UpYunException {
		if (completeListener == null) {
			throw new UpYunException("completeListener should not be null.");
		}
		BlockUploader blockUploader = new BlockUploader(httpManager, host, bucket, localFile, blockSize, expiration, policy, signature, progressListener, completeListener);
		
		AsyncRun.run(blockUploader);
	}
	
	/**
	 * 获取文件元信息 计算policy、signature需要此信息
	 * 
	 * @param localFile
	 * @param savePath
	 * @return
	 * @throws UpYunException
	 * @throws FileNotFoundException
	 */
	public Map<String, Object> fetchFileInfoDictionaryWith(File localFile, String savePath)
			throws UpYunException, FileNotFoundException {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put(Params.PATH, savePath);
		paramsMap.put(Params.EXPIRATION, expiration);
		paramsMap.put(Params.BLOCK_NUM, UpYunUtils.getBlockNum(localFile, blockSize));
		paramsMap.put(Params.FILE_SIZE, localFile.length());
		paramsMap.put(Params.FILE_MD5, UpYunUtils.md5Hex(new FileInputStream(localFile)));

		return paramsMap;
	}

	// get & set
	public String getBucket() {
		return this.bucket;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	
	/**
	 * @param connectTimeout 单位：s 默认60s
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.httpManager.setConnectTimeout(connectTimeout * 1000);
	}
	
	/**
	 * @param responseTimeout 单位：s 默认60s
	 */
	public void setResponseTimeout(int responseTimeout) {
		this.httpManager.setResponseTimeout(responseTimeout * 1000);
	}
}
