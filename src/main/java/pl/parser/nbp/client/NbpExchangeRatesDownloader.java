package pl.parser.nbp.client;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import pl.parser.nbp.model.Currency;
import pl.parser.nbp.model.CurrencyRate;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.vavr.API.For;

@RequiredArgsConstructor
public class NbpExchangeRatesDownloader {

  private final NbpClient nbpClient;
  private final FileListClient fileListClient;

  public Try<List<CurrencyRate>> getCurrencyRates(
      Currency currency, LocalDate startDate, LocalDate endDate) {
    return List.range(startDate.getYear(), endDate.plusYears(1).getYear())
        .map(fileListClient::collectFilesNamesForYear)
        .reduce((t1, t2) -> For(t1, t2).yield(List::appendAll))
        .flatMap(
            list ->
                list.filter(name -> isBuyAndSaleFileBetweenDates(name, startDate, endDate))
                    .map(name -> nbpClient.getCurrencyRate(name, currency))
                    .foldLeft(
                        Try.success(List.empty()), (t1, t2) -> For(t1, t2).yield(List::append)));
  }

  private boolean isBuyAndSaleFileBetweenDates(
      String fileName, LocalDate startDate, LocalDate endDate) {
    Pattern pattern = Pattern.compile("c\\d{3}z(\\d{2})(\\d{2})(\\d{2})");
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.find()) {
      int year = Integer.valueOf(matcher.group(1)) + 2000;
      int month = Integer.valueOf(matcher.group(2));
      int day = Integer.valueOf(matcher.group(3));
      LocalDate curDate = LocalDate.of(year, month, day);
      return !curDate.isBefore(startDate) && !curDate.isAfter(endDate);
    }
    return false;
  }
}
