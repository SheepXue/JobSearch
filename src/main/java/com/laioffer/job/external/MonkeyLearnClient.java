package com.laioffer.job.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.ExtractRequestBody;
import com.laioffer.job.entity.ExtractResponseItem;
import com.laioffer.job.entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {
    private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "3b5f33fa9714eac4e9545ad16df028ac633f3f7d";

    // use List<Sting> rather than String as they all independently job description
    // every article will fit a Set<String> result
    public List<Set<String>> extract(List<String> articles) {

        for(int i = 0; i < articles.size(); i++) {
            int size = 256 / articles.size();
            articles.set(i, articles.get(i).substring(0, size));
        }
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(EXTRACT_URL);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Authorization", "Token " + AUTH_TOKEN);
        ExtractRequestBody body = new ExtractRequestBody(articles, 100);

        String jsonBody = "";
        try {
            jsonBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            request.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ResponseHandler<List<Set<String>>> responseHandler = response -> {
            if(response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if(entity == null) {
                return Collections.emptyList();
            }
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);
            List<Set<String>> keywordList = new ArrayList<>();
            for(ExtractResponseItem result : results) {
                Set<String> keywords = new HashSet<>();
                for(Extraction extraction : result.extractions) {
                    keywords.add(extraction.parsedValue);
                }
                keywordList.add(keywords);
            }
            return keywordList;
        };
        try {
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // unit test
    public static void main(String[] args) {
        List<String> articles = Arrays.asList( "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.",
                "Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
                "The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
        );
        MonkeyLearnClient client = new MonkeyLearnClient();
        List<Set<String>> keywordList = client.extract(articles);
        System.out.println(keywordList);
        System.out.println(keywordList.size());
    }

}
