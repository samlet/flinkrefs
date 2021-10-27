package com.bluecc.refs.ecommerce;

import com.bluecc.refs.ecommerce.beans.Party;
import com.bluecc.refs.ecommerce.beans.Person;
import com.bluecc.refs.ecommerce.beans.RateAmount;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DimJoinPrinter {
    public static void main(String[] args) throws Exception {
        DimJoinPrinter printer = new DimJoinPrinter();
        printer.printDim();
    }

    public void printDim() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> ds = env.fromElements("admin", "DemoEmployee");
        SingleOutputStreamOperator<PartyWide> partyDS = AsyncDataStream.unorderedWait(
                        ds, new DimInjector.EntityDim<>(Party.class, "party"),
                        60, TimeUnit.SECONDS).returns(Party.class)
                .map(p -> PartyWide.builder()
                        .id(p.getPartyId())
                        .build());

        partyDS.print("party");

        SingleOutputStreamOperator<PartyWide> partyWideDS = AsyncDataStream.unorderedWait(
                partyDS,
                new DimInjector.TypedEntityDim<PartyWide, Person>(
                        Person.class, "person", "party_id") {
                    @Override
                    public String getKey(PartyWide input) {
                        return input.getId();
                    }

                    @Override
                    public void join(PartyWide input, List<Person> rowData) {
                        Person p = rowData.get(0);
                        input.setFirstName(p.getFirstName());
                        input.setLastName(p.getLastName());
                    }
                },
                60,
                TimeUnit.SECONDS
        );

        SingleOutputStreamOperator<PartyWide> partyWithRateWideDS = AsyncDataStream.unorderedWait(
                partyWideDS,
                new DimInjector.TypedEntityDim<PartyWide, RateAmount>(
                        RateAmount.class, "rate_amount", "party_id") {
                    @Override
                    public String getKey(PartyWide input) {
                        return input.getId();
                    }

                    @Override
                    public void join(PartyWide input, List<RateAmount> rowData) {
                        RateAmount p = rowData.get(0);
                        input.setRateAmount(p.getRateAmount());
                    }
                },
                60,
                TimeUnit.SECONDS
        );

        partyWithRateWideDS.print("wide");
        env.execute();
    }
}

@Data
@Builder
class PartyWide {
    String id;
    String lastName;
    String firstName;
    BigDecimal rateAmount;
}