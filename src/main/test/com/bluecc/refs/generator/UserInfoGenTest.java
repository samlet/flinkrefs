package com.bluecc.refs.generator;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UserInfoGenTest {

    @Test
    public void genUserInfos() {
        UserInfoGen gen=new UserInfoGen("50", new Date(), true);
        gen.genUserInfos(10).forEach(u-> System.out.println(u));
    }
}

