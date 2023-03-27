module dev.dotspace.common {
  requires static lombok;
  requires static org.jetbrains.annotations;

  //Base Library
  exports dev.dotspace.common;

  //Annotations
  exports dev.dotspace.common.annotation;

  //Java concurrent bridge
  exports dev.dotspace.common.concurrent;

  //Exceptions
  exports dev.dotspace.common.exception;

  //Lists: Pagination
  exports dev.dotspace.common.list;

  //Math
  exports dev.dotspace.common.math;

  //Response package
  exports dev.dotspace.common.response;
}