package com.php25.desktop.repostars.controller;

import com.php25.desktop.repostars.service.AppService;
import com.php25.desktop.repostars.util.GlobalUtil;
import com.php25.desktop.repostars.util.LocalStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author penghuiping
 * @date 2020/9/22 14:36
 */
@Slf4j
@Component
public class NavController extends BaseController {

    @FXML
    public Button myRepoBtn;
    @FXML
    public Button allStarRepoBtn;
    @FXML
    public Button selfDefinedGroupBtn;
    @FXML
    public Button refreshDataBtn;
    @FXML
    private Button logoutBtn;

    @Autowired
    private LocalStorage localStorage;

    @Autowired
    private AppService appService;

    @Override
    public void start() throws Exception {
        logoutBtn.setOnMouseClicked(this);
        myRepoBtn.setOnMouseClicked(this);
        allStarRepoBtn.setOnMouseClicked(this);
        selfDefinedGroupBtn.setOnMouseClicked(this);
        refreshDataBtn.setOnMouseClicked(this);
    }

    @Override
    public void handleMouseEvent(MouseEvent mouseEvent) throws Exception {
        Button button = (Button) mouseEvent.getSource();
        switch (button.getId()) {
            case "logoutBtn": {
                GlobalUtil.goNextScene("controller/login_controller.fxml", mouseEvent, this.applicationContext);
                localStorage.clearAll();
                break;
            }
            case "myRepoBtn": {
                GlobalUtil.goNextScene("controller/my_repo_controller.fxml", mouseEvent, this.applicationContext);
                break;
            }
            case "allStarRepoBtn": {
                GlobalUtil.goNextScene("controller/all_star_repo_controller.fxml", mouseEvent, this.applicationContext);
                break;
            }
            case "selfDefinedGroupBtn": {
                GlobalUtil.goNextScene("controller/group_controller.fxml", mouseEvent, this.applicationContext);
                break;
            }
            case "refreshDataBtn": {
                log.info("refresh data start...");
                var user = localStorage.getLoginUser();
                var result = appService.syncStarRepo(user.getLogin(), user.getToken());
                break;
            }
            default: {
                break;
            }
        }
    }
}
