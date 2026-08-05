package com.VsmartEngine.MediaJungle;

import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Aspect
@Component
public class LoggingAspect {
	
	 private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	 // GDPR-TASK-21: Logging must not leak personal data or secrets into log files (GDPR
	 // Art. 5(1)(f) integrity & confidentiality, Art. 32 security of processing). This aspect
	 // previously logged raw method arguments and full return values verbatim, which could
	 // include passwords, JWTs, emails and phone numbers. Everything logged from here now goes
	 // through redactPii() first.
	 private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[\\w.-]+");
	 private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
	 private static final Pattern PASSWORD_FIELD_PATTERN =
	         Pattern.compile("(?i)(password|passwd|pwd)\\s*=\\s*[^,\\]\\}]+");

	 private String redactPii(Object value) {
	     if (value == null) {
	         return "null";
	     }
	     String text = String.valueOf(value);
	     text = PASSWORD_FIELD_PATTERN.matcher(text).replaceAll("$1=***REDACTED***");
	     text = JWT_PATTERN.matcher(text).replaceAll("***REDACTED-JWT***");
	     text = EMAIL_PATTERN.matcher(text).replaceAll("***REDACTED-EMAIL***");
	     return text;
	 }

	 @Pointcut("within(com.VsmartEngine.MediaJungle.FrontController)")
	 public void frontControllerMethods() {}
	 
	// Log the request before the method execution
	    @Before("frontControllerMethods()")
	    public void logBefore(JoinPoint joinPoint) {
	        // GDPR-TASK-21: redact arguments before they are written to disk.
	        logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature().getName(), redactPii(java.util.Arrays.toString(joinPoint.getArgs())));
	    }

	    // Log the response after the method execution
	    @AfterReturning(pointcut = "frontControllerMethods()", returning = "result")
	    public void logAfterReturning(JoinPoint joinPoint, Object result) {
	        // GDPR-TASK-21: redact the return value before logging - controller responses often
	        // contain the exact personal-data payload sent to the client.
	        logger.info("Exiting method: {} with result: {}", joinPoint.getSignature().getName(), redactPii(result));
	    }

	    // Log exceptions thrown by the method
	    @AfterThrowing(pointcut = "frontControllerMethods()", throwing = "exception")
	    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
	        logger.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), redactPii(exception.getMessage()), exception);
	    }


}
