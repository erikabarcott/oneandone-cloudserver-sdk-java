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
package com.oneandone.sdk;

import com.oneandone.rest.POJO.Requests.CreateVPNRequest;
import com.oneandone.rest.POJO.Requests.UpdateVPNRequest;
import com.oneandone.rest.POJO.Response.VPNConfigurationResponse;
import com.oneandone.rest.POJO.Response.VPNResponse;
import com.oneandone.rest.client.RestClientException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Ali
 */
public class VPNApi extends OneAndOneAPIBase {

    public VPNApi() {
        super("vpns", "configuration_file");
    }

    /**
     * Returns a list of your VPNs.
     *
     * @param page Allows to use pagination. Sets the number of servers that
     * will be shown in each page.
     * @param perPage Current page to show.
     * @param sort Allows to sort the result by priority:sort=name retrieves a
     * list of elements ordered by their names.sort=-creation_date retrieves a
     * list of elements ordered according to their creation date in descending
     * order of priority.
     * @param query Allows to search one string in the response and return the
     * elements that contain it. In order to specify the string use parameter q:
     * q=My server
     * @param fields Returns only the parameters requested:
     * fields=id,name,description,hardware.ram
     * @return VPNResponse[]
     * @throws RestClientException
     * @throws IOException
     */
    public List<VPNResponse> getVPNs(int page, int perPage, String sort, String query, String fields) throws RestClientException, IOException {
        String queryUrl = getUrlBase().concat(resource).concat("?");
        boolean firstParameter = true;

        if (page != 0) {
            if (!firstParameter) {
                queryUrl = queryUrl.concat("&");
            }
            queryUrl = queryUrl.concat("page=").concat(Integer.toString(page));
            firstParameter = false;
        }
        if (perPage != 0) {
            if (!firstParameter) {
                queryUrl = queryUrl.concat("&");
            }
            queryUrl = queryUrl.concat("per_page=").concat(Integer.toString(perPage));
            firstParameter = false;
        }
        if (sort != null && !sort.isEmpty()) {
            if (!firstParameter) {
                queryUrl = queryUrl.concat("&");
            }
            queryUrl = queryUrl.concat("sort=").concat(sort);
            firstParameter = false;
        }
        if (query != null && !query.isEmpty()) {
            if (!firstParameter) {
                queryUrl = queryUrl.concat("&");
            }
            queryUrl = queryUrl.concat("q=").concat(query);
            firstParameter = false;
        }
        if (fields != null && !fields.isEmpty()) {
            if (!firstParameter) {
                queryUrl = queryUrl.concat("&");
            }
            queryUrl = queryUrl.concat("fields=").concat(fields);
        }
        VPNResponse[] result = client.get(queryUrl, null, VPNResponse[].class);
        return Arrays.asList(result);
    }

    /**
     * Returns information about a VPN.
     *
     * @param vpnId Unique VPN identifier
     * @return VPNResponse
     * @throws RestClientException
     * @throws IOException
     */
    public VPNResponse getVPN(String vpnId) throws RestClientException, IOException {
        return client.get(getUrlBase().concat(resource).concat("/").concat(vpnId), null, VPNResponse.class);
    }

    /**
     * Download your VPN configuration file.
     *
     * @param vpnId Unique VPN identifier
     * @return Configuration File
     * @throws RestClientException
     * @throws IOException
     */
    public void getVPNConfigurationFile(String vpnId, String filePath) throws RestClientException, IOException {
        VPNConfigurationResponse result = client.get(getUrlBase().concat(resource).concat("/").concat(vpnId).concat("/").concat(parentResource), null, VPNConfigurationResponse.class);
        DateFormat df = new SimpleDateFormat("ddMMyy HHmmss");
        Date dateobj = new Date();
        String date = df.format(dateobj);
        if (!filePath.isEmpty()) {
            if (filePath.contains(".zip")) {
                DownloadConfigurationFile(result.getConfig_zip_file(), filePath);
            } else {
                DownloadConfigurationFile(result.getConfig_zip_file(), filePath+ ".zip");
            }
        } else {
            DownloadConfigurationFile(result.getConfig_zip_file(), "C:\\VPNConfiguration" + date + ".zip");
        }
    }

    private void DownloadConfigurationFile(String codedFile, String fileName) throws FileNotFoundException, MalformedURLException, IOException {
        String _fileName = fileName; //The file that will be saved on your computer
        //Code to download
        InputStream in = new ByteArrayInputStream(codedFile.getBytes("UTF-8"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = Base64.decodeBase64(out.toByteArray());

        FileOutputStream fos = new FileOutputStream(_fileName);
        fos.write(response);
        fos.close();
        //End download code

        System.out.println("Finished");
    }

    /**
     * Adds a new VPN.
     *
     * @param object CreateVPNRequest
     * @return VPNResponse
     * @throws RestClientException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public VPNResponse createVPN(CreateVPNRequest object) throws RestClientException, IOException {
        return client.create(getUrlBase().concat(resource), object, VPNResponse.class, 202);
    }

    /**
     * Removes a VPN.
     *
     * @param vpnId Unique VPN identifier
     * @return VPNResponse
     * @throws RestClientException
     * @throws IOException
     */
    public VPNResponse deleteVPN(String vpnId) throws RestClientException, IOException {
        return client.delete(getUrlBase().concat(resource).concat("/").concat(vpnId), VPNResponse.class);
    }

    /**
     * Modify VPN configuration file.
     *
     * @param vpnId Unique VPN identifier
     * @param object UpdatePublicIP
     * @return VPNResponse
     * @throws RestClientException
     * @throws IOException
     */
    public VPNResponse updateVPN(String vpnId, UpdateVPNRequest object) throws RestClientException, IOException {
        return client.update(getUrlBase().concat(resource).concat("/").concat(vpnId), object, VPNResponse.class, 200);
    }
}
