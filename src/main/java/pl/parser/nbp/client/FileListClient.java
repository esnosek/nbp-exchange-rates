package pl.parser.nbp.client;

import io.vavr.collection.List;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FileListClient {

  public Try<List<String>> collectFilesNamesForYear(Integer year) {
    return Try.of(() -> new URL("http://www.nbp.pl/kursy/xml/dir" + year + ".txt"))
        .flatMap(
            url ->
                Try.withResources(() -> new BufferedReader(new InputStreamReader(url.openStream())))
                    .of(br -> List.ofAll(br.lines())));
  }
}
