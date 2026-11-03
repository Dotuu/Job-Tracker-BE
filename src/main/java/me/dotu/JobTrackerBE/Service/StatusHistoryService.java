package me.dotu.JobTrackerBE.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Repository.StatusHistoryRepository;
import me.dotu.JobTrackerBE.Response.StatusHistoryResponse;

@Service
public class StatusHistoryService {
  @Autowired
  private StatusHistoryRepository statusHistoryRepo; 

  public List<StatusHistoryResponse> getStatusHistory(String clerkUserId){
    return statusHistoryRepo.findAllByUserId(clerkUserId)
    .stream()
    .map(StatusHistoryResponse::from)
    .toList();
  }
}
