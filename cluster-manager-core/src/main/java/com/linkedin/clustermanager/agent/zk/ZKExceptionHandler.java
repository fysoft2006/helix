package com.linkedin.clustermanager.agent.zk;

import org.apache.log4j.Logger;

public class ZKExceptionHandler
{
  private static ZKExceptionHandler instance = new ZKExceptionHandler();
  private static Logger logger = Logger.getLogger(ZKExceptionHandler.class);
  private ZKExceptionHandler()
  {

  }

  void handle(Exception e)
  {

  logger.error(e);
  }

  public static ZKExceptionHandler getInstance()
  {
    return instance;
  }
}