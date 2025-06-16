package com.udea.fe.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {

  private Long feedbackId;
  private String comment;
  private Integer rating;
  private Long createdById;
  private Long submissionId;
  private Long parentFeedbackId;
}
