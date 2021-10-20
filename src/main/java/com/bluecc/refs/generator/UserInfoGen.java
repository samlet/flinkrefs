package com.bluecc.refs.generator;

import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserInfoGen implements IInfoGen<UserInfo>, Serializable {
    String maleRate;
    Date birthdayStart;
    boolean genId;
    int currentId=0;

    public UserInfoGen(String maleRate, Date birthdayStart, boolean genId) {
        this.maleRate = maleRate;
        this.birthdayStart = birthdayStart;
        this.genId=genId;
    }

    public UserInfo initUserInfo(int index){
        Date date=this.birthdayStart;
        Integer maleRateWeight = ParamUtil.checkRatioNum(this.maleRate);

        UserInfo userInfo = new UserInfo();
        if(genId){
            userInfo.setId((long) index);
        }
        String email = RandomEmail.getEmail(6, 12);
        String loginName = email.split("@")[0];
        userInfo.setLoginName(loginName);
        userInfo.setEmail(email);
        userInfo.setGender( new RandomOptionGroup(new RanOpt("M",maleRateWeight),new RanOpt("F",100-maleRateWeight)).getRandStringValue());
        String lastName = RandomName.insideLastName(userInfo.getGender());
        userInfo.setName(RandomName.getChineseFamilyName()+lastName);
        userInfo.setNickName(RandomName.getNickName(userInfo.getGender(),lastName));
        userInfo.setBirthday(DateUtils.addMonths(date, -1*RandomNum.getRandInt(15,55)*12));

        userInfo.setCreateTime(date);
        userInfo.setUserLevel(new RandomOptionGroup(new RanOpt("1",7),new RanOpt("2",2),new RanOpt("3",1)).getRandStringValue());
        userInfo.setPhoneNum("13"+RandomNumString.getRandNumString(1,9,9,""));
        return userInfo;
    }

    public List<UserInfo> genUserInfos(int count){
        List<UserInfo> userInfoList=new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userInfoList.add( initUserInfo(i)) ;
        }
        return userInfoList;
    }

    public UserInfo next(){
        currentId++;
        return initUserInfo(currentId);
    }
}
