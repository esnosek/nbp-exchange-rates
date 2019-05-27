package pl.parser.nbp;

import io.vavr.collection.List;
import io.vavr.control.Try;
import pl.parser.nbp.client.CurrencyRateDeserializer;
import pl.parser.nbp.client.FileListClient;
import pl.parser.nbp.client.NbpClient;
import pl.parser.nbp.client.NbpExchangeRatesDownloader;
import pl.parser.nbp.infrastructure.CurrencyStatsCalculator;
import pl.parser.nbp.model.Currency;
import pl.parser.nbp.model.CurrencyRate;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MainClass {

  public static void main(String[] args) {

    if (args.length != 3) {
      System.out.println("Incorrect numbers of input arguments!");
      return;
    }
    if (!Arrays.asList("EUR", "USD", "CHF", "GBP").contains(args[0])) {
      System.out.println("Currency " + args[0] + " is not allowed");
      System.out.println("Supported currency: EUR, USD, CHF, GBP");
      return;
    }
    if (!args[1].matches("\\d{4}-\\d{2}-\\d{2}")) {
      System.out.println("Start publicationDate is not in correct format!");
      System.out.println("Correct format is yyyy-mm-dd");
      return;
    }
    if (!args[2].matches("\\d{4}-\\d{2}-\\d{2}")) {
      System.out.println("End publicationDate is not in correct format!");
      System.out.println("Correct format is yyyy-mm-dd");
      return;
    }

    Currency currency = Currency.valueOf(args[0]);
    LocalDate startDate = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate endDate = LocalDate.parse(args[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    NbpExchangeRatesDownloader downloader =
        new NbpExchangeRatesDownloader(
            new NbpClient(new CurrencyRateDeserializer()), new FileListClient());

    CurrencyStatsCalculator stats = new CurrencyStatsCalculator();

    DecimalFormat formatter = new DecimalFormat("#.####");

    downloader
        .getCurrencyRates(currency, startDate, endDate)
        .onFailure(Throwable::printStackTrace)
        .onSuccess(
            rates -> {
              if (rates.isEmpty()) {
                System.out.println("There is nothing to show between given dates");
              } else {
                stats
                    .getAverageExchangeRate(rates)
                    .onSuccess(mean -> System.out.println(formatter.format(mean)))
                    .onFailure(f -> System.out.println("Error when calculating average rates"));
                stats
                    .getStandardDeviation(rates)
                    .onSuccess(stddev -> System.out.println(formatter.format(stddev)))
                    .onFailure(
                        f -> System.out.println("Error when calculating standard deviation"));
              }
            });
  }
}
