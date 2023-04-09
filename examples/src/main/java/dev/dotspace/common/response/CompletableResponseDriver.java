package dev.dotspace.common.response;

import org.jetbrains.annotations.Nullable;

import java.io.*;
/**
 * Example class for dev.dotspace.common.response.
 */
public final class CompletableResponseDriver {

  public static void main(String[] args) {

    /*Read content From file.*/
    readBytesOfFile(new File("./testFile.txt"))
      .sniff((state, bytes, throwable) -> {
        System.out.printf("""

          Detailed info of file:
          State of process: %s,
          Byte length: %s,
          Thrown error: %s

          """, state, bytes != null ? bytes.length : 0, throwable);
      })
      .ifPresent(bytes -> {
        System.out.printf("Read file successfully [length: %s]", bytes.length);
      })

      //Filter will create a new instance of CompletableResponse and return it.
      .filter(bytes -> bytes.length == 0 /*Filter if byte length is 0*/)

      //Else will create a new instance of CompletableResponse and return it.
      .elseUse(() -> new byte[0] /*Use these byte if absent (also in error case).*/)

      //Map will create a new instance of CompletableResponse and return it.
      .map(ByteArrayInputStream::new /*Convert to byte input stream*/)

      .ifPresent(byteArrayInputStream -> {
        /*Use my bytes here*/
      });

  }

  public static CompletableResponse<byte[]> readBytesOfFile(@Nullable final File file) {
    return new CompletableResponse<byte[]>().completeAsync(() -> {
      if (file == null || file.exists()) {
        throw new NullPointerException("File does not exists."); //-> Completes instance exceptionally.
      }

      if (file.isFile()) {
        throw new RuntimeException("File is not a readable file!"); //-> Completes instance exceptionally.
      }

      try (final FileInputStream fileInputStream = new FileInputStream(file)) {
        return fileInputStream.readAllBytes(); //Complete with content.
      } catch (final IOException exception) {
        throw new RuntimeException("Error while reading content of file!", exception); //-> Completes instance exceptionally.
      }
    });
  }

}
