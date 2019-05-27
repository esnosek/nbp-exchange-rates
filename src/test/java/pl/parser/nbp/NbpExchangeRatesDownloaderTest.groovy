package pl.parser.nbp

import io.vavr.collection.List
import io.vavr.control.Try
import pl.parser.nbp.client.CurrencyRateDeserializer
import pl.parser.nbp.client.FileListClient
import pl.parser.nbp.client.NbpClient
import pl.parser.nbp.client.NbpExchangeRatesDownloader
import pl.parser.nbp.infrastructure.CurrencyStatsCalculator
import pl.parser.nbp.model.Currency
import pl.parser.nbp.model.CurrencyRate
import spock.lang.Specification

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NbpExchangeRatesDownloaderTest extends Specification {

    def "test if standard deviation for sale rate is calculated correctly"() {
        given: "currency, startDate and endDate"
        def currency = Currency.EUR
        def startDate =  LocalDate.parse("2013-01-28", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        def endDate =  LocalDate.parse("2013-01-31", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        CurrencyStatsCalculator stats = new CurrencyStatsCalculator()
        def downloader = new NbpExchangeRatesDownloader(
                new NbpClient(new CurrencyRateDeserializer()),
                new FileListClient())

        when: "calculate currency rates for given period"

        Try<List<CurrencyRate>> currencyRates = downloader.getCurrencyRates(currency,startDate,endDate)

        then: "check if standard deviation is calculated correctly"

        currencyRates.collect{stats.getStandardDeviation(it)}
            .collect{it.getOrElse(new BigDecimal("0"))}
            .collect{new DecimalFormat("#.####").format(it)}
            .first() == "0,0125"
    }

    def "test if average buy rates is calculated correctly"() {
        given: "currency, startDate and endDate"
        def currency = Currency.EUR
        def startDate =  LocalDate.parse("2013-01-28", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        def endDate =  LocalDate.parse("2013-01-31", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        CurrencyStatsCalculator stats = new CurrencyStatsCalculator()
        def downloader = new NbpExchangeRatesDownloader(
                new NbpClient(new CurrencyRateDeserializer()),
                new FileListClient())

        when: "calculate currency rates for given period"

        Try<List<CurrencyRate>> currencyRates = downloader.getCurrencyRates(currency,startDate,endDate)

        then: "check if standard deviation is calculated correctly"

        currencyRates.collect{stats.getAverageExchangeRate(it)}
                .collect{it.getOrElse(new BigDecimal("0"))}
                .collect{new DecimalFormat("#.####").format(it)}
                .first() == "4,1505"
    }
}
