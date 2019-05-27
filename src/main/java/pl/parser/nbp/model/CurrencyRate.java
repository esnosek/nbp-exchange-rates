package pl.parser.nbp.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Value
public class CurrencyRate {
  private LocalDate publicationDate;
  private Currency currency;
  private BigDecimal buyRate;
  private BigDecimal saleRate;
}
