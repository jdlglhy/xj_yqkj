package com.ry.yqkj.common.utils.uuid;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnowflakeIdUtil {
    private static Integer workerId = 1;

    static {
        try {
            initWorkerId();
        } catch (Exception e) {
            log.error("IdGeneratorSnowflake static Exception:", e);
            throw e;
        }
        IdGeneratorOptions options = new IdGeneratorOptions(workerId.shortValue());
        options.WorkerIdBitLength = 8;
        options.SeqBitLength = 4;
        YitIdHelper.setIdGenerator(options);
    }

    /**
     * 生成id
     */
    public static long nextId() {
        return YitIdHelper.nextId();
    }

    /**
     * workerId取ip地址最后1段
     */
    private static void initWorkerId() {
        String host = NetUtil.getLocalhostStr();
        if (!Validator.isIpv4(host)) {
            log.warn("invalid ipv4 host, IdGeneratorSnowflake initWorkerId host:{}, workerId:{}", host, workerId);
            return;
        }
        String[] ipSplit = host.split("\\.");
        //
        workerId = Integer.parseInt(ipSplit[3]);
        log.info("IdGeneratorSnowflake initWorkerId host:{}, workerId:{}", host, workerId);
    }
}
