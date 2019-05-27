package pl.parser.nbp.client;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.parser.nbp.model.Currency;
import pl.parser.nbp.model.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.vavr.API.For;

public class CurrencyRateDeserializer {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public Try<CurrencyRate> apply(Document document, Currency currency) {

    Try<LocalDate> publicationDate = parseLocalDate("data_publikacji", document);
    Try<Element> element =
        List.ofAll(document.getElementsByTag("kod_waluty"))
            .filter(code -> code.text().equals(currency.name()))
            .headOption()
            .map(Element::parent)
            .toTry();
    Try<BigDecimal> buyRate = element.flatMap(el -> parseBigDecimal("kurs_kupna", el));
    Try<BigDecimal> saleRate = element.flatMap(el -> parseBigDecimal("kurs_sprzedazy", el));

    return For(publicationDate, buyRate, saleRate)
        .yield(
            (p, b, s) ->
                CurrencyRate.builder()
                    .publicationDate(p)
                    .currency(currency)
                    .buyRate(b)
                    .saleRate(s)
                    .build());
  }

  Try<BigDecimal> parseBigDecimal(String name, Element element) {
    return Try.of(
        () -> new BigDecimal(element.getElementsByTag(name).first().text().replaceAll(",", ".")));
  }

  Try<LocalDate> parseLocalDate(String name, Document document) {
    return Try.of(() -> LocalDate.parse(document.getElementsByTag(name).first().text(), FORMATTER));
  }
}
