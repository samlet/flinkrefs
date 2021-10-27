package com.bluecc.refs.ecommerce;

import com.alibaba.fastjson.JSONObject;
import com.bluecc.refs.ecommerce.beans.Party;
import com.bluecc.refs.ecommerce.beans.Person;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

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

        DataStream<String> ds = env.fromElements("admin");
        SingleOutputStreamOperator<PartyWide> partyDS = AsyncDataStream.unorderedWait(
                        ds, new OfbizDim.EntityDim<>(Party.class, "party"),
                        60, TimeUnit.SECONDS).returns(Party.class)
                .map(p -> PartyWide.builder()
                        .id(p.getPartyId())
                        .build());

        partyDS.print("party");

        SingleOutputStreamOperator<PartyWide> partyWideDS = AsyncDataStream.unorderedWait(
                partyDS,
                new OfbizDim.TypedEntityDim<PartyWide, Person>(
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

        partyWideDS.print("wide");
        env.execute();
    }
}

@Data
@Builder
class PartyWide {
    String id;
    String lastName;
    String firstName;
}