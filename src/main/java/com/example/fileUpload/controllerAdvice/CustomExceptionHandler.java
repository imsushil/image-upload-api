package com.example.fileUpload.controllerAdvice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler  {
	
	@Value("#{'${allowed.origins}'.split(',')}")
	List<String> allowedOrigins;
	
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<String> handler(MultipartException e, HttpServletRequest req) {
		HttpHeaders headers = new HttpHeaders();
		if(this.allowedOrigins.size() > 1) {
			headers.add("Access-Control-Allow-Origin", "*");
		} else {
			headers.add("Access-Control-Allow-Origin", this.allowedOrigins.get(0));
		}
		String message;
		if(e.getMessage().contains("FileSizeLimitExceededException")) {
			message = "Maximum upload size of file can be 1MB.";
		} else {
			message = e.getMessage();
		}
		return new ResponseEntity<String>(message, headers, HttpStatus.BAD_REQUEST);
	}
}
