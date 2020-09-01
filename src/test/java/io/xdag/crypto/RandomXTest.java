package io.xdag.crypto;

import io.xdag.crypto.jni.RandomX;
import io.xdag.utils.BytesUtils;
import org.junit.Test;

import java.util.Random;

public class RandomXTest {

    @Test
    public void rxCacheTest(){
        final String key="hello rx";
        final long rxCache= RandomX.allocCache();
        System.out.printf("alloc cache address 0x" + Long.toHexString(rxCache));

        RandomX.initCache(rxCache,key.getBytes(),key.length());

        RandomX.releaseCache(rxCache);
        System.out.printf("release cache address 0x" + Long.toHexString(rxCache));
    }

    @Test
    public void initDataSetTest(){
        final String key="hello rx 1";
        final long rxCache= RandomX.allocCache();
        RandomX.initCache(rxCache,key.getBytes(),key.length());
        System.out.printf("release cache address 0x" + Long.toHexString(rxCache));

        final long rxDataSet=RandomX.allocDataSet();
        RandomX.initDataSet(rxCache,rxDataSet,1);

        RandomX.releaseCache(rxCache);
        RandomX.releaseDataSet(rxDataSet);
    }

    @Test
    public void initDataSetMutiTest(){
        final String key="hello rx 1";
        final long rxCache= RandomX.allocCache();
        RandomX.initCache(rxCache,key.getBytes(),key.length());
        System.out.printf("release cache address 0x" + Long.toHexString(rxCache));

        final long rxDataSet=RandomX.allocDataSet();
        RandomX.initDataSet(rxCache,rxDataSet,4);

        final long rxVm=RandomX.createVm(rxCache,rxDataSet,4);

        final String data="hello world 1";
        byte[] bs= RandomX.calculateHash(rxVm,data.getBytes(),data.getBytes().length);
        System.out.println("get randomx hash " + BytesUtils.toHexString(bs));
        RandomX.releaseCache(rxCache);
        RandomX.releaseDataSet(rxDataSet);
        RandomX.destroyVm(rxVm);
    }

    @Test
    public void allocVmTest(){

    }

    @Test
    public void hashTest(){

    }

    @Test
    public void changeSeedTest(){

    }

}
