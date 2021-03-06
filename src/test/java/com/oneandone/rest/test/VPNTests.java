/*
 * Copyright 2016 Ali.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.rest.test;

import com.oneandone.rest.POJO.Requests.CreateVPNRequest;
import com.oneandone.rest.POJO.Requests.UpdateVPNRequest;
import com.oneandone.rest.POJO.Response.DataCenter;
import com.oneandone.rest.POJO.Response.VPNResponse;
import com.oneandone.rest.client.RestClientException;
import com.oneandone.sdk.OneAndOneApi;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ali
 */
public class VPNTests {

    static OneAndOneApi oneandoneApi = new OneAndOneApi();
    static Random rand = new Random();
    static List<VPNResponse> vpns;
    static VPNResponse vpn;

    @BeforeClass
    public static void getVPNs() throws RestClientException, IOException {
        oneandoneApi.setToken(System.getenv("OAO_TOKEN"));
        createVPN();
        List<VPNResponse> result = oneandoneApi.getVpnApi().getVPNs(0, 0, null, null, null);
        vpns = result;
        assertNotNull(result);
    }

    @Test
    public void getVPN() throws RestClientException, IOException {
        VPNResponse result = oneandoneApi.getVpnApi().getVPN(vpns.get(0).getId());

        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    public void getVPNConfiguration() throws RestClientException, IOException {
        oneandoneApi.getVpnApi().getVPNConfigurationFile(vpns.get(0).getId(), "C:\\temp");
    }

    public static void createVPN() throws RestClientException, IOException {
        String randomValue = rand.nextInt(99) + "test.java";

        CreateVPNRequest request = new CreateVPNRequest();
        request.setName(randomValue);
        request.setDescription(randomValue + "desc");
        List<DataCenter> datacenters = oneandoneApi.getDataCenterApi().getDataCenters(0, 0, null, null, null);
        if (!datacenters.isEmpty()) {
            request.setDatacenterId(datacenters.get(0).getId());

            vpn = oneandoneApi.getVpnApi().createVPN(request);
            assertNotNull(vpn);
            assertNotNull(vpn.getId());
        }
    }

    @Test
    public void updateVPN() throws RestClientException, IOException, InterruptedException {
        String randomValue = rand.nextInt(99) + "update.java";
        UpdateVPNRequest request = new UpdateVPNRequest();
        request.setName(randomValue);
        VPNResponse result = oneandoneApi.getVpnApi().updateVPN(vpn.getId(), request);

        assertNotNull(result);
        assertNotNull(result.getId());
        //check if the public ip updated
        VPNResponse ipResult = oneandoneApi.getVpnApi().getVPN(result.getId());
        assertNotNull(ipResult.getId());
    }

    @AfterClass
    public static void deleteVPN() throws RestClientException, IOException, InterruptedException {
        VPNResponse result = oneandoneApi.getVpnApi().deleteVPN(vpn.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
    }
}
