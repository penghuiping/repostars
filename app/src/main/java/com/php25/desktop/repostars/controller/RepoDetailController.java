package com.php25.desktop.repostars.controller;

import com.php25.common.core.mess.LruCache;
import com.php25.common.core.util.StringUtil;
import com.php25.desktop.repostars.service.UserService;
import com.php25.desktop.repostars.service.dto.GistDto;
import com.php25.desktop.repostars.util.GlobalUtil;
import com.php25.desktop.repostars.util.LocalStorage;
import com.php25.github.ReposManager;
import com.php25.github.dto.RepoReadme;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * @author penghuiping
 * @date 2020/10/14 11:11
 */
@Slf4j
@Component
public class RepoDetailController extends BaseController {

    public Button backBtn;
    public Label titleLabel;
    public Long id;
    public String title;
    public Scene previousScene;
    public TextArea container;

    @Autowired
    private LruCache<String, Object> lruCache;

    @Autowired
    private LocalStorage localStorage;

    @Autowired
    private ReposManager reposManager;

    @Autowired
    private UserService userService;

    @Override
    public void start() throws Exception {
        backBtn.setOnMouseClicked(this);
        titleLabel.setText(title);
        //先看lru缓存中是否存在
        Object htmlObj = lruCache.getValue(title);
        if (null == htmlObj || StringUtil.isBlank(htmlObj.toString())) {
            //在看本地数据库中是否存在
            GistDto tbGist = userService.findOneByFullName(title);
            if (null != tbGist && StringUtil.isNotBlank(tbGist.getReadme())) {
                var content = tbGist.getReadme();
                lruCache.putValue(title, content);
                htmlObj = content;
            }

            if (null == htmlObj) {
                var user = localStorage.getLoginUser();
                RepoReadme repoReadme = reposManager.getRepoReadme(user.getToken(), title);
                if (null == repoReadme) {
                    return;
                }
                var content = new String(Base64.getMimeDecoder().decode(repoReadme.getContent()));
                tbGist.setReadme(content);
                userService.updateGist(tbGist);
                htmlObj = content;
            }
        }
        String html = htmlObj.toString();
        container.setText(html);
    }

    @Override
    public void handleMouseEvent(MouseEvent mouseEvent) throws Exception {
        if (mouseEvent.getSource() instanceof Button) {
            var button = (Button) mouseEvent.getSource();
            switch (button.getId()) {
                case "backBtn": {
                    GlobalUtil.goNextScene(mouseEvent, previousScene);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}
