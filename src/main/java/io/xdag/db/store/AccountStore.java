/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.db.store;

import static io.xdag.utils.FastByteComparisons.equalBytes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.spongycastle.util.encoders.Hex;

import io.xdag.core.Address;
import io.xdag.core.Block;
import io.xdag.core.XdagField;
import io.xdag.crypto.ECKey;
import io.xdag.db.KVSource;
import io.xdag.utils.BytesUtils;
import io.xdag.wallet.Wallet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountStore {
    private static final byte[] ACCOUNT_ORIGIN_KEY = Hex.decode("FFFFFFFFFFFFFFFF");
    private static final byte[] ACCOUNT_GLOBAL_BALANCE = Hex.decode("EEEEEEEEEEEEEEEE");
    private static final byte[] ACCOUNT_GLOBAL_MINER = Hex.decode("FFFFFFFFFFFFFFFE");
    /** <hash->nexthash> */
    private KVSource<byte[], byte[]> accountSource;
    private BlockStore blockStore;
    private Wallet wallet;

    public AccountStore(
            Wallet wallet, BlockStore blockStore, KVSource<byte[], byte[]> accountSource) {
        this.wallet = wallet;
        this.accountSource = accountSource;
        this.blockStore = blockStore;
    }

    public void init() {
        this.accountSource.init();
    }

    public void reset() {
        this.accountSource.reset();
    }

    /** 存放第一个地址块 */
    public synchronized void addFirstAccount(Block block, int keyIndex) {
        log.debug(
                "Add new account:"
                        + Hex.toHexString(block.getHashLow())
                        + " singed by "
                        + keyIndex
                        + " key in wallet");
        accountSource.put(ACCOUNT_ORIGIN_KEY, block.getHashLow());
        blockStore.updateBlockKeyIndex(block.getHashLow(), keyIndex);
    }

    /** 账户形成链表 */
    public synchronized void addNewAccount(Block block, int keyIndex) {
        // 第一个
        if (getAllAccount().size() == 0) {
            log.debug("Global miner");
            accountSource.put(ACCOUNT_GLOBAL_MINER, block.getHash());
        }
        log.debug(
                "Add new account:"
                        + Hex.toHexString(block.getHashLow())
                        + " singed by "
                        + keyIndex
                        + " key in wallet");
        accountSource.put(block.getHashLow(), accountSource.get(ACCOUNT_ORIGIN_KEY));
        accountSource.put(ACCOUNT_ORIGIN_KEY, block.getHashLow());
        blockStore.updateBlockKeyIndex(block.getHashLow(), keyIndex);
    }

    public synchronized void removeAccount(Block block) {
        log.debug("Remove an account:" + Hex.toHexString(block.getHashLow()));
        byte[] value = accountSource.get(block.getHashLow());
        byte[] key = ACCOUNT_ORIGIN_KEY;
        while (!equalBytes(accountSource.get(key), block.getHashLow())) {
            key = accountSource.get(key);
        }
        accountSource.put(key, value);
        accountSource.delete(block.getHashLow());

        // TODO:block store
        blockStore.deleteBlockKeyIndex(block.getHashLow());
    }

    /** 返回满足的地址 Address<send amount+192bit hash> */
    public Map<Address, ECKey> getAccountListByAmount(long amount) {
        Map<Address, ECKey> result = new LinkedHashMap<>();
        byte[] first = accountSource.get(ACCOUNT_ORIGIN_KEY);
        long res = amount;
        while (res > 0 && first != null) {
            long amountRelease = blockStore.getBlockInfoByHash(first).getAmount();
            if (amountRelease > 0) {
                ECKey key = wallet.getKeyByIndex(blockStore.getBlockKeyIndex(first));
                long sendValue = Math.min(amountRelease, res);
                result.put(new Address(first, XdagField.FieldType.XDAG_FIELD_IN, sendValue), key);
                res -= amountRelease;
            }

            first = accountSource.get(first);
        }
        // 没有足够的账户去支付
        if (res > 0) {
            log.debug("No match account");
            return null;
        }

        return result;
    }

    public Block getAccountBlockByHash(byte[] hashlow, boolean isRaw) {
        return blockStore.getBlockByHash(hashlow, isRaw);
    }

    public void updateGBanlance(long amount) {
        if (accountSource.get(ACCOUNT_GLOBAL_BALANCE) == null) {
            accountSource.put(ACCOUNT_GLOBAL_BALANCE, BytesUtils.longToBytes(amount, false));
        } else {
            long global = BytesUtils.bytesToLong(accountSource.get(ACCOUNT_GLOBAL_BALANCE), 0, false);
            accountSource.put(ACCOUNT_GLOBAL_BALANCE, BytesUtils.longToBytes(amount + global, false));
        }
    }

    public long getGBalance() {
        if (accountSource.get(ACCOUNT_GLOBAL_BALANCE) == null) {
            return 0;
        } else {
            return BytesUtils.bytesToLong(accountSource.get(ACCOUNT_GLOBAL_BALANCE), 0, false);
        }
    }

    public List<byte[]> getAllAccount() {
        List<byte[]> res = new ArrayList<>();
        byte[] first = accountSource.get(ACCOUNT_ORIGIN_KEY);
        while (first != null) {
            res.add(first);
            first = accountSource.get(first);
        }
        return res;
    }

    public byte[] getGlobalMiner() {
        return accountSource.get(ACCOUNT_GLOBAL_MINER);
    }
}
