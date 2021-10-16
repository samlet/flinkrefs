package com.bluecc.fixtures;

public class PropertyProcs {
    public static void main(String[] args) {
        String val=Modules.build().getInstance(PropertyFac.class).getStrValue("hbase.zookeeper.quorum");
        System.out.println(val);
    }
}
