package com.qube.api.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

public class API_Factory {

	//Generate Token and Paste it here every time during test
	static String token = "a004ba6b-e7fe-4015-989d-b9100ed1972a";
	
	//APIs
	protected static String deleteApi = "https://ec2-13-127-159-5.ap-south-1.compute.amazonaws.com/sharebox/api/files?fileId=%s&token="+token;
	protected static String getUploadDetails = "https://ec2-13-127-159-5.ap-south-1.compute.amazonaws.com/sharebox/api/files?token="+token;
	protected static String postUploadDetails = "https://ec2-13-127-159-5.ap-south-1.compute.amazonaws.com/sharebox/api/upload?token="+token;
	static String putFileUploadStatus = "https://ec2-13-127-159-5.ap-south-1.compute.amazonaws.com/sharebox/api/upload?fileId=%s&bytesCompleted=%s&token="+token;

	static HttpResponse httpStatusResponse;
	
	//JSON Body
	static String name = null;
	static String size = null;
	static String hash = null;
	
	
	static public String getRandomName() {
		Random rand = new Random();
		int  n = rand.nextInt(5000) + 1;
		System.out.println("Name is : "+"Karthick "+n);
		return "Karthick "+n;
	}
	
	static public String getRandomSize() {
		Random rand = new Random();
		int  n = rand.nextInt(5000) + 1;
		System.out.println("Size is : "+n);
		return String.valueOf(n);
	}
	
	static public String getRandomHash() {
		Random random = new Random();
        int val = random.nextInt();
        String Hex = new String();
        Hex = Integer.toHexString(val);
        System.out.println("Hash Value is : "+Hex);
        return Hex;
	}

	//To ignore cacert issues
	protected static CloseableHttpClient createAcceptSelfSignedCertificateClient(){
		try{
		SSLContext sslContext = org.apache.http.ssl.SSLContextBuilder.create()
				.loadTrustMaterial(new TrustSelfSignedStrategy()).build();
		HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
		SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
		return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
		}
		catch(Exception e){
			return null;
		}
	}
	
	static protected void validateFileUploadStatus(String message) {
		Assert.assertTrue(message.equals("Bytes has been updated successfully"));
	}
	
	static protected String generateJsonData(String name, String fileSize, String hash) {
		return "{\"name\":\""+name+"\",\"size\":\""+fileSize+"\",\"hash\":\""+hash+"\"}";
	}
	
	static protected String generatePutEndPoint(String fileId, String bytes) {
		return String.format(putFileUploadStatus, fileId, bytes);
	}
	
	public static String getFileId(String name, String jsonArrays) {
		String fileId = null;
		JSONArray array = new JSONArray(jsonArrays);
		for (int i = 1; i <= array.length(); i++) {
			String obj_name = array.getJSONObject(i).getString("name");
			if(obj_name.equals(name)){
				fileId = array.getJSONObject(i).getString("fileId").trim();
				break;
			}
			else
				continue;				
		}
		Assert.assertTrue(fileId != null, "fileId is null");
		return fileId;
	}
	
	static public String getUploadDetails(String endPoint) {
		BufferedReader bufferedReader = null;
		String jsonResponse = "";
		try  {
			HttpGet httpget = new HttpGet(endPoint);
			System.out.println("Executing request " + httpget.getRequestLine());
			httpStatusResponse = createAcceptSelfSignedCertificateClient().execute(httpget);
			bufferedReader = new BufferedReader(
					new InputStreamReader(httpStatusResponse.getEntity().getContent(), "UTF-8"));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				jsonResponse += output;
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Json Response : "+jsonResponse);
		System.out.println("----------------------------------------");
		return jsonResponse;
	}
	

	static public void postUpload(String endPoint, String requestData) {
		BufferedReader bufferedReader = null;
		String jsonResponse = "";
		try  {
			HttpPost httpPost = new HttpPost(endPoint);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "application/json");
			StringEntity entity = new StringEntity(requestData, ContentType.APPLICATION_JSON);
			entity.setContentType("application/json");
			httpPost.setEntity(entity);			
			System.out.println("Executing request " + httpPost.getRequestLine());
			httpStatusResponse = createAcceptSelfSignedCertificateClient().execute(httpPost);
			bufferedReader = new BufferedReader(
					new InputStreamReader(httpStatusResponse.getEntity().getContent(), "UTF-8"));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				jsonResponse += output;
			}
			System.out.println("Response : " + jsonResponse);
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static public String putUpload(String endPoint) {
		BufferedReader bufferedReader = null;
		String jsonResponse = "";
		try  {
			HttpPut httpPut = new HttpPut(endPoint);
			httpPut.setHeader("Content-Type", "application/json");
			httpPut.addHeader("Accept", "application/json");
			System.out.println("Executing request " + httpPut.getRequestLine());
			httpStatusResponse = createAcceptSelfSignedCertificateClient().execute(httpPut);
			System.out.println("Done");
			bufferedReader = new BufferedReader(
					new InputStreamReader(httpStatusResponse.getEntity().getContent(), "UTF-8"));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				jsonResponse += output;
			}
			System.out.println("Response : " + jsonResponse);
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new JSONObject(jsonResponse).getString("message").trim();
	}
	
	static public String deleteFile(String endPoint, String fileId) {
		BufferedReader bufferedReader = null;
		String jsonResponse = "";
		try  {
			HttpDelete httpDelete = new HttpDelete(String.format(endPoint, fileId));
			httpDelete.setHeader("Content-Type", "application/json");
			httpDelete.addHeader("Accept", "application/json");
			System.out.println("Executing request " + httpDelete.getRequestLine());
			httpStatusResponse = createAcceptSelfSignedCertificateClient().execute(httpDelete);
			System.out.println("Done");
			bufferedReader = new BufferedReader(
					new InputStreamReader(httpStatusResponse.getEntity().getContent(), "UTF-8"));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				jsonResponse += output;
			}
			System.out.println("Response : " + jsonResponse);
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return jsonResponse;
	}
	
	static public void isFileDeleted(String message) {
		Assert.assertTrue(message.contains("File deleted successfully"));
	}

}
