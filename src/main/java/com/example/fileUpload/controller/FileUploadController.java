package com.example.fileUpload.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fileUpload.models.FormDataRequest;

@RestController
public class FileUploadController {
	
	@Value("${upload.path}")
	String uploadPath;
	
	@RequestMapping(value="/submit", method=RequestMethod.POST)
	public ResponseEntity<String> upload(@ModelAttribute FormDataRequest request) {
		String fileName = request.getFile().getOriginalFilename();
		try {
			Files.copy(request.getFile().getInputStream(), Paths.get(uploadPath + fileName), StandardCopyOption.REPLACE_EXISTING);
			return new ResponseEntity<String>("{\"message\": \"success\"}", HttpStatus.OK);
		} catch(IOException e) {
			return new ResponseEntity<String>("Could not upload. Reason=" + e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/file/{fileName}")
	public ResponseEntity<?> getFile(@PathVariable("fileName") String fileName) {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(uploadPath + fileName));
			return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
		} catch(IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/delete/all", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteFiles() throws IOException {
		File directoryToBeDeleted = new File(uploadPath);
		FileUtils.cleanDirectory(directoryToBeDeleted); 
	    return new ResponseEntity<String>("All files deleted successfully.", HttpStatus.OK);
	}
}
