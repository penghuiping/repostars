package com.php25.desktop.repostars.controller;

import com.php25.desktop.repostars.service.UserService;
import com.php25.desktop.repostars.service.dto.ReposDto;
import com.php25.desktop.repostars.service.dto.UserDto;
import com.php25.desktop.repostars.util.GlobalUtil;
import com.php25.desktop.repostars.util.LocalStorage;
import com.php25.desktop.repostars.view.RepoListCell;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/10/9 10:35
 */
@Component
public class MyRepoController extends BaseController {

    public VBox container;
    public ScrollPane scrollPane;
    public Button backBtn;

    @Autowired
    private UserService userService;

    @Autowired
    private LocalStorage localStorage;

    @Override
    public void start() throws Exception {
        scrollPane.getStyleClass().add("edge-to-edge");
        backBtn.setOnMouseClicked(this);
        UserDto tbUser = localStorage.getLoginUser();
        List<ReposDto> tbReposList = userService.getMyRepos(tbUser.getLogin(), tbUser.getToken());
        if (null != tbReposList && !tbReposList.isEmpty()) {
            for (ReposDto tbRepos : tbReposList) {
                RepoListCell repoListCell = new RepoListCell(
                        tbRepos.getId(),
                        tbRepos.getFullName(),
                        tbRepos.getDescription(),
                        tbRepos.getWatchers().toString(),
                        tbRepos.getForks().toString()
                );
                container.getChildren().add(repoListCell);
            }
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent mouseEvent) throws Exception {
        Button button = (Button) mouseEvent.getSource();
        switch (button.getId()) {
            case "backBtn": {
                GlobalUtil.goNextScene("controller/nav_controller.fxml", mouseEvent, this.applicationContext);
                break;
            }
            default: {
                break;
            }
        }
    }
}
