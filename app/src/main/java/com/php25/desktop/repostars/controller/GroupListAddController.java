package com.php25.desktop.repostars.controller;

import com.php25.common.core.dto.DataGridPageDto;
import com.php25.desktop.repostars.service.AppService;
import com.php25.desktop.repostars.service.dto.GistDto;
import com.php25.desktop.repostars.service.dto.UserDto;
import com.php25.desktop.repostars.util.GlobalUtil;
import com.php25.desktop.repostars.util.LocalStorage;
import com.php25.desktop.repostars.view.RepoListCell;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/10/13 16:09
 */
@Slf4j
@Component
public class GroupListAddController extends BaseController {

    public Button backBtn;
    public Label titleLabel;
    public TextField searchTextField;
    public Button searchBtn;
    public ScrollPane scrollPane;
    public VBox container;

    public Scene previousScene;

    @Autowired
    private AppService userService;

    @Autowired
    private LocalStorage localStorage;

    public Long groupId;

    public String groupTitle;


    @Override
    public void start() throws Exception {
        scrollPane.getStyleClass().add("edge-to-edge");
        titleLabel.setText(groupTitle);
        backBtn.setOnMouseClicked(this);
        searchBtn.setOnMouseClicked(this);
        loadItem("");
    }

    @Override
    public void handleMouseEvent(MouseEvent mouseEvent) throws Exception {
        if (mouseEvent.getSource() instanceof Button) {
            Button button = (Button) mouseEvent.getSource();
            switch (button.getId()) {
                case "backBtn": {
                    GlobalUtil.goNextScene(mouseEvent, this.previousScene);
                    GroupListController controller = this.applicationContext.getBean(GroupListController.class);
                    controller.loadEditStatus();
                    break;
                }
                case "searchBtn": {
                    String searchKey = searchTextField.getText();
                    loadItem(searchKey);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private void loadItem(String searchKey) {
        this.container.getChildren().clear();
        UserDto tbUser = this.localStorage.getLoginUser();
        final AtomicInteger pageNum = new AtomicInteger(1);
        Integer pageSize = 10;
        DataGridPageDto<GistDto> dataGridPageDto = userService.getMyGistUngroup(tbUser.getLogin(), searchKey,
                PageRequest.of(pageNum.get(), pageSize));
        List<GistDto> tbGists = dataGridPageDto.getData();
        var list = tbGists.stream()
                .map(tbGist -> new RepoListCell(tbGist.getId(), tbGist.getFullName()
                        , tbGist.getDescription()
                        , tbGist.getWatchers().toString()
                        , tbGist.getForks().toString()))
                .collect(Collectors.toList());
        list.forEach(repoListCell -> {
            repoListCell.setOnMouseClicked(mouseEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("注意");
                alert.setHeaderText(String.format("确定把此项加入\"%s\"分组么", this.groupTitle));
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                    userService.addOneGistIntoGroup(repoListCell.id, this.groupId);
                    this.container.getChildren().remove(repoListCell);
                }
            });
        });
        this.container.getChildren().addAll(list);

        this.scrollPane.setOnScroll(scrollEvent -> {
            //下滑
            if (scrollEvent.getDeltaY() < 0 && pageNum.get() < dataGridPageDto.getRecordsTotal() / pageSize) {
                var dataGridPageDto1 = userService.getMyGistUngroup(tbUser.getLogin(), searchKey,
                        PageRequest.of(pageNum.incrementAndGet(), pageSize));
                List<GistDto> gistList1 = dataGridPageDto1.getData();
                if (null != gistList1 && !gistList1.isEmpty()) {
                    List<RepoListCell> repoListCells = new ArrayList<>();
                    for (GistDto tbGist : gistList1) {
                        RepoListCell repoListCell = new RepoListCell(
                                tbGist.getId(),
                                tbGist.getFullName(),
                                tbGist.getDescription(),
                                tbGist.getWatchers().toString(),
                                tbGist.getForks().toString()
                        );
                        repoListCell.setOnMouseClicked(mouseEvent -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("注意");
                            alert.setHeaderText(String.format("确定把此项加入\"%s\"分组么", this.groupTitle));
                            Optional<ButtonType> buttonType = alert.showAndWait();
                            if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                                userService.addOneGistIntoGroup(repoListCell.id, this.groupId);
                                this.container.getChildren().remove(repoListCell);
                            }
                        });
                        repoListCells.add(repoListCell);
                    }
                    container.getChildren().addAll(repoListCells);
                }
            }
        });

    }
}
