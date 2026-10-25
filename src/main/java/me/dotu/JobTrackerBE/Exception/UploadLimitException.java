package me.dotu.JobTrackerBE.Exception;

public class UploadLimitException extends RuntimeException{
  public UploadLimitException(String message){
    super(message);
  }
}
