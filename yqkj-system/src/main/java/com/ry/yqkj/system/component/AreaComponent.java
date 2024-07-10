//package com.ry.yqkj.system.component;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.google.common.collect.Maps;
//import com.ry.yqkj.common.core.redis.RedisCache;
//import com.ry.yqkj.system.domain.Area;
//import com.ry.yqkj.system.mapper.AreaMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.rmi.ServerException;
//import java.util.List;
//import java.util.Map;
//
//import static org.apache.commons.compress.utils.IOUtils.toByteArray;
//
//@Component
//@Slf4j
//public class AreaComponent {
//
//    @Resource
//    private RedisCache redisCache;
//
//    @Resource
//    private ResourceLoader resourceLoader;
//
//    @PostConstruct
//    public void init() throws IOException {
//        String jsonString = null;
//        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:area.json");
//        try {
//            jsonString = new String(toByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            log.error("AreaComponent#init error", e);
//            throw new ServerException("读取区域json配置异常！");
//        }
//        List<Area> areaList = Lists.newArrayList();
//        JSONObject jsonObject = JSON.parseObject(jsonString);
//        Map<String, Area> map = Maps.newHashMap();
//        JSONArray jsonArray = jsonObject.getJSONArray("0");
//        //省、直辖市
//        jsonArray.forEach(o -> {
//            JSONObject data = (JSONObject) o;
//            data.forEach((k, v) -> {
//                Area area = Area.builder()
//                        .code(k)
//                        .name(v.toString())
//                        .level(0)
//                        .parentCode("0")
//                        .accessor("0," + k)
//                        .build();
//                areaList.add(area);
//                //市
//                String cityKey = "0," + k;
//                JSONArray cityArray = jsonObject.getJSONArray(cityKey);
//                cityArray.forEach(c -> {
//                    JSONObject cityData = (JSONObject) c;
//                    cityData.forEach((ccode, cname) -> {
//                        Area city = Area.builder()
//                                .code(ccode)
//                                .name(cname.toString())
//                                .level(1)
//                                .parentCode(k)
//                                .accessor(cityKey + "," + ccode)
//                                .build();
//                        areaList.add(city);
//                        //县、区
//                        String countyKey = cityKey + "," + ccode;
//                        JSONArray countyArray = jsonObject.getJSONArray(countyKey);
//                        countyArray.forEach(cou -> {
//                            JSONObject countyData = (JSONObject) cou;
//                            countyData.forEach((couCode, couName) -> {
//                                Area county = Area.builder()
//                                        .code(couCode)
//                                        .name(couName.toString())
//                                        .level(2)
//                                        .parentCode(ccode)
//                                        .accessor(countyKey + "," + couCode)
//                                        .build();
//                                areaList.add(county);
//                            });
//
//                        });
//                    });
//                });
//            });
//        });
//        log.info("size={}",areaList.size());
//        int count = 0;
//        for(Area area : areaList){
//            areaMapper.insert(area);
//            count ++ ;
//        }
//        log.info("result={}",count);
//    }
//
//    @Resource
//    private AreaMapper areaMapper;
//}