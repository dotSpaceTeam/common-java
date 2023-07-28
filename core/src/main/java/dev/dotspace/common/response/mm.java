package dev.dotspace.common.response;

public class mm {

  public static void main(String[] args) {

    ResponseService responseService = ResponseService.handled(throwable -> {
      System.out.println("Error on " + throwable);
    });

    responseService.newInstance().completeExceptionally(new NullPointerException("Test"));

  }
}
