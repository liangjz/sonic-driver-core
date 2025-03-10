/*
 *  Copyright (C) [SonicCloudOrg] Sonic Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.cloud.sonic.driver.common.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.cloud.sonic.driver.common.models.BaseResp;
import org.cloud.sonic.driver.common.service.BaseClient;
import org.cloud.sonic.driver.ios.models.IOSRect;
import org.cloud.sonic.driver.common.service.WebElement;
import org.cloud.sonic.driver.common.tool.Logger;
import org.cloud.sonic.driver.common.tool.SonicRespException;

import java.util.Base64;

public class WebElementImpl implements WebElement {
    private String id;
    private BaseClient baseClient;
    private Logger logger;

    public WebElementImpl(String id, BaseClient baseClient) {
        this.id = id;
        this.baseClient = baseClient;
        logger = baseClient.getLogger();
    }

    @Override
    public void click() throws SonicRespException {
        baseClient.checkSessionId();
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createPost(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/click"));
        if (b.getErr() == null) {
            logger.info("click element %s.", id);
        } else {
            logger.error("click element %s failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }

    @Override
    public void sendKeys(String text) throws SonicRespException {
        sendKeys(text, 3);
    }

    @Override
    public void sendKeys(String text, int frequency) throws SonicRespException {
        baseClient.checkSessionId();
        JSONObject data = new JSONObject();
        data.put("value", text.split(""));
        data.put("frequency", frequency);
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createPost(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/value")
                        .body(data.toJSONString()), 60000);
        if (b.getErr() == null) {
            logger.info("send key to %s.", id);
        } else {
            logger.error("send key to %s failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }

    @Override
    public void clear() throws SonicRespException {
        baseClient.checkSessionId();
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createPost(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/clear"), 60000);
        if (b.getErr() == null) {
            logger.info("clear %s.", id);
        } else {
            logger.error("clear %s failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }

    @Override
    public String getText() throws SonicRespException {
        baseClient.checkSessionId();
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createGet(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/text"));
        if (b.getErr() == null) {
            logger.info("get %s text %s.", id, b.getValue().toString());
            return b.getValue().toString();
        } else {
            logger.error("get %s text failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }

    @Override
    public IOSRect getRect() throws SonicRespException {
        baseClient.checkSessionId();
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createGet(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/rect"));
        if (b.getErr() == null) {
            IOSRect iosRect = JSON.parseObject(b.getValue().toString(), IOSRect.class);
            logger.info("get %s rect %s.", id, iosRect.toString());
            return iosRect;
        } else {
            logger.error("get %s rect failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }

    @Override
    public byte[] screenshot() throws SonicRespException {
        baseClient.checkSessionId();
        BaseResp b = baseClient.getRespHandler().getResp(
                HttpUtil.createGet(baseClient.getRemoteUrl() + "/session/"
                        + baseClient.getSessionId() + "/element/" + id + "/screenshot"), 60000);
        if (b.getErr() == null) {
            logger.info("get element %s screenshot.", id);
            return Base64.getMimeDecoder().decode(b.getValue().toString());
        } else {
            logger.error("get element %s screenshot failed.", id);
            throw new SonicRespException(b.getErr().getMessage());
        }
    }
}
