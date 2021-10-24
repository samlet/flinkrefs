package com.bluecc.refs.fastjson;

import java.util.List;

public class Project {
    String pjName;
    boolean waibao;
    public boolean isWaibao() {
        return waibao;
    }
    public void setWaibao(boolean waibao) {
        this.waibao = waibao;
    }
    List<Factory> l_factory;
    //List<Worker> worker;
    public String getPjName() {
        return pjName;
    }
    public void setPjName(String pjName) {
        this.pjName = pjName;
    }
    public List<Factory> getL_factory() {
        return l_factory;
    }
    public void setL_factory(List<Factory> l_factory) {
        this.l_factory = l_factory;
    }


}