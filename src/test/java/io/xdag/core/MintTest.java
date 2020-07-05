package io.xdag.core;

import io.xdag.config.Config;
import org.junit.Test;

import static io.xdag.config.Constants.*;
import static io.xdag.utils.BasicUtils.xdag2amount;


public class MintTest {
    @Test
    public void testMint(){

        int num = 196250;
        long reward = getCurrentReward();
        long reward1 = getReward(0,num);

        System.out.println(reward);
        System.out.println(reward1);


    }

    /** 根据当前区块数量计算奖励金额 cheato * */
    public long getCurrentReward() {
        return xdag2amount(1024);
    }

    public long getReward(long time,long num){
        long start = getStartAmount(time,num);
        long amount = start >> (num >> MAIN_BIG_PERIOD_LOG);
        return amount;
    }

    private long getStartAmount(long time,long num){
        long forkHeight = Config.MainNet?MAIN_APOLLO_HEIGHT:MAIN_APOLLO_TESTNET_HEIGHT;
        long startAmount = 0;
        if(num >= forkHeight){
//      if(g_apollo_fork_time == 0) {
//        g_apollo_fork_time = time;
//      }
            startAmount = MAIN_APOLLO_AMOUNT;
        }else {
            startAmount = MAIN_START_AMOUNT;
        }

        return startAmount;
    }
}
