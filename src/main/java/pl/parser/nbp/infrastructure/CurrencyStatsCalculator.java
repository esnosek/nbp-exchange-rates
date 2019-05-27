package pl.parser.nbp.infrastructure;

import com.jidesoft.utils.BigDecimalMathUtils;
import io.vavr.collection.List;
import io.vavr.control.Try;
import pl.parser.nbp.model.CurrencyRate;

import java.math.BigDecimal;
import java.math.MathContext;

public class CurrencyStatsCalculator {

  public Try<BigDecimal> getStandardDeviation(List<CurrencyRate> currencyRates) {
    return Try.of(
        () ->
            BigDecimalMathUtils.stddev(
                currencyRates.map(CurrencyRate::getSaleRate).toJavaList(), false, MathContext.DECIMAL64));
  }

  public Try<BigDecimal> getAverageExchangeRate(List<CurrencyRate> currencyRates) {
    return Try.of(
        () ->
            BigDecimalMathUtils.mean(
                currencyRates.map(CurrencyRate::getBuyRate).toJavaList(), MathContext.DECIMAL64));
  }
}
