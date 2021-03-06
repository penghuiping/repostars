package com.php25.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.JsonUtil;
import com.php25.github.dto.Gist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/9/22 16:34
 */
@Service
@Slf4j
public class GistManagerImpl implements GistManager {

    @Autowired
    private HttpClient httpClient;

    @Override
    public List<Gist> getAllStarredGist(String username, String token, Integer pageNum, Integer pageSize) {
        try {
            var uri = new URI(String.format(Constants.LIST_STARRED_GISTS + "?page=%d&&per_page=%d", username, pageNum, pageSize));
            var request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(Constants.TIMEOUT))
                    .GET()
                    .header("Authorization", String.format("token %s", token))
                    .header("Accept", "application/vnd.github.v3+json").build();
            var response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            var result = response.get(3 * Constants.TIMEOUT, TimeUnit.SECONDS);
            return JsonUtil.fromJson(result.body(), new TypeReference<List<Gist>>() {
            });
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("获取starred项目列表失败", e);
        }
    }


}
