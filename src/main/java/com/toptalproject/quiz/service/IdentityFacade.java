package com.toptalproject.quiz.service;


/**
 * The interface for IdentityFacade.
 */
public interface IdentityFacade {
  /**
   * The method that exposes the LoggedInUser.
   *
   * @return LoggedInUser.
   */
  String getLoggedInUserId();
}