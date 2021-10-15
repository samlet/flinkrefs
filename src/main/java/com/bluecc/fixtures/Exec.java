package com.bluecc.fixtures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

// $ mvn compile exec:java -Dexec.mainClass="com.bluecc.fixtures.Exec"
// $ mvn compile exec:java -Dexec.mainClass="com.bluecc.fixtures.Exec" \
//  -Dexec.args="First Second"
public class Exec {

    private static final Logger LOGGER = LoggerFactory.getLogger(Exec.class);

    public static void main(String[] args) {
        LOGGER.info("Running the main method");
        if (args.length > 0) {
            LOGGER.info("List of arguments: {}", Arrays.toString(args));
        }
    }
}
