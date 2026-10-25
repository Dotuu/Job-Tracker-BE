package me.dotu.JobTrackerBE.Exception;

import org.springframework.http.HttpStatus;

public class InvalidFileException extends RuntimeException{
  private final HttpStatus status;

  public InvalidFileException(HttpStatus status, String message){
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus(){
    return status;
  }
}
