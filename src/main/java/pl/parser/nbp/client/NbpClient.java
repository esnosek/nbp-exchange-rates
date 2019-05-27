package pl.parser.nbp.client;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import pl.parser.nbp.model.Currency;
import pl.parser.nbp.model.CurrencyRate;

@RequiredArgsConstructor
public class NbpClient {

  private final CurrencyRateDeserializer currencyRateDeserializer;

  // TODO String.format, pliki konfiguracyjne
  public Try<CurrencyRate> getCurrencyRate(String name, Currency currency) {
    return Try.of(() -> Jsoup.connect("http://www.nbp.pl/kursy/xml/" + name + ".xml").get())
        .flatMap(document -> currencyRateDeserializer.apply(document, currency));
  }
}
