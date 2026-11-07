package me.dotu.JobTrackerBE.Exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
    String detail = e.getBindingResult().getFieldErrors().stream()
      .map(error -> error.getField() + " " + error.getDefaultMessage())
      .collect(Collectors.joining("; "));
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail.isEmpty() ? "Invalid request" : detail);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ProblemDetail handleMaxSize(MaxUploadSizeExceededException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONTENT_TOO_LARGE, "File must be 5 MB or smaller");
  }

  @ExceptionHandler(MultipartException.class)
  public ProblemDetail handleMultipart(MultipartException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid upload request");
  }

  @ExceptionHandler(InvalidFileException.class)
  public ProblemDetail handleInvalidFile(InvalidFileException e) {
    return ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler(UploadLimitException.class)
  public ProblemDetail handleUploadLimit(UploadLimitException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(StorageException.class)
  public ProblemDetail handleStorage(StorageException e) {
    log.error("Storage failure", e);
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process file");
  }
}
