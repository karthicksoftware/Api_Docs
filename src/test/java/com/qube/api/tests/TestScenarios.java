package com.qube.api.tests;

import java.io.IOException;

import com.qube.api.core.API_Factory;
/**
 * Qube Cinemas Technologies API Automation Assignment
 * @author Karthick Arunachalam
 *
 */
public class TestScenarios extends API_Factory{

	public static void main(String... a) throws UnsupportedOperationException, IOException, InterruptedException {

		String name = getRandomName();
		
		String fileSize = getRandomSize();
		
		String hash = getRandomHash();
		
		//POST
		postUpload(postUploadDetails, generateJsonData(name, fileSize, hash));
		
		//GET
		String responseOfFiles = getUploadDetails(getUploadDetails);
		
		String fileId = getFileId(name, responseOfFiles);
		
		//PUT
		String message = putUpload(generatePutEndPoint(fileId, fileSize));		
		
		validateFileUploadStatus(message);
		
		System.out.println("Waiting for 10 seconds, please check file name : "+ name +" is created in dashboard");
		
		Thread.sleep(15000);
		
		//DELETE
		String responseDelete = deleteFile(deleteApi, fileId);
		
		System.out.println("Del : "+responseDelete);
		
		isFileDeleted(responseDelete);		
		
	}
	
}
