package com.php25.github;

import com.php25.github.dto.RepoReadme;
import com.php25.github.dto.Repos;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/9/23 13:33
 */
public interface ReposManager {

    /**
     * 获取个人的repos列表
     *
     * @param token github个人oauth token
     * @return repos列表
     */
    List<Repos> getReposList(String token);

    /**
     * 获取某个repo的readme
     *
     * @param token        github个人oauth token
     * @param repoFullName repo全名
     * @return readme内容
     */
    RepoReadme getRepoReadme(String token, String repoFullName);
}
