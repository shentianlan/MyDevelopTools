package cn.shentianlan.mydeveloptools.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 编辑文本控制器
 *
 * @author shentianlan
 * @version 1.0
 */
public class EditTextController {

    double start_x;     //切图区域的起始位置x
    double start_y;     //切图区域的起始位置y
    double w;           //切图区域宽
    double h;           //切图区域高
    HBox hBox;          //切图区域

    private Stage primaryStage;

    @FXML
    private TextArea editTextArea;

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * 将多行文本替换成一行
     * 原理是去除换行符号，使用空格代替
     */
    @FXML
    private void mergeText() {
        //是否存在选中的数据
        boolean isSelectedText = StrUtil.isNotBlank(editTextArea.getSelectedText());

        //存在选中的数据则取选中的数据，否则取整个文本框的数据T
        String sourceText = isSelectedText ? editTextArea.getSelectedText() : editTextArea.getText();

        //linux中换行符为\n\r,Windows系统的换行符为\n
        String mergeText = sourceText.replaceAll("\r", "").replaceAll("\n", " ");

        if (isSelectedText) {
            mergeText = editTextArea.getText().replace(editTextArea.getSelectedText(), mergeText);
        }
        editTextArea.setText(mergeText);
    }

    /**
     * 识别二维码
     */
    @FXML
    private void extractQrCode(ActionEvent actionEvent) {
        primaryStage.hide();

        Stage qrCodeStage = new Stage();
        //锚点布局采用半透明
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: #85858522");
        //场景设置白色全透明
        Scene scene = new Scene(anchorPane);
        scene.setFill(Paint.valueOf("#ffffff00"));
        qrCodeStage.setScene(scene);
        //清楚全屏中间提示文字
        qrCodeStage.setFullScreenExitHint("");
        qrCodeStage.initStyle(StageStyle.TRANSPARENT);
        qrCodeStage.setFullScreen(true);
        qrCodeStage.setAlwaysOnTop(true);
        qrCodeStage.show();
        //切图窗口绑定鼠标按下事件
        anchorPane.setOnMousePressed(event -> {
            //清除锚点布局中所有子元素
            anchorPane.getChildren().clear();
            //创建切图区域
            hBox = new HBox();
            //设置背景保证能看到切图区域桌面
            hBox.setBackground(null);
            //设置边框
            hBox.setBorder(new Border(new BorderStroke(Paint.valueOf("#c03700"), BorderStrokeStyle.SOLID,
                    null,new BorderWidths(3))));
            anchorPane.getChildren().add(hBox);
            //记录并设置起始位置
            start_x = event.getSceneX();
            start_y = event.getSceneY();
            AnchorPane.setLeftAnchor(hBox,start_x);
            AnchorPane.setTopAnchor(hBox,start_y);
        });
        //绑定鼠标按下拖拽的事件
        anchorPane.setOnMouseDragged(event -> {
            //用label记录切图区域的长宽
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(30);
            label.setPrefWidth(170);
            anchorPane.getChildren().add(label);
            AnchorPane.setLeftAnchor(label,start_x+30);
            AnchorPane.setTopAnchor(label,start_y);
            label.setTextFill(Paint.valueOf("#ffffff"));//白色填充
            label.setStyle("-fx-background-color: #000000");//黑背景
            //计算宽高并且完成切图区域的动态效果
            w = Math.abs(event.getSceneX()-start_x);
            h = Math.abs(event.getSceneY()-start_y);
            hBox.setPrefWidth(w);
            hBox.setPrefHeight(h);
            label.setText("宽："+w+" 高："+h);
        });
        //绑定鼠标松开事件
        anchorPane.setOnMouseReleased(event -> {
            //记录最终长宽
            w = Math.abs(event.getSceneX()-start_x);
            h = Math.abs(event.getSceneY()-start_y);
            //切图辅助舞台消失
            qrCodeStage.close();
            try {
                //切图具体方法
                captureScreen(start_x,start_y,w,h);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //主舞台还原
            primaryStage.show();
        });


    }


    private void captureScreen(double x, double y, double width, double height) {
        try {
            Robot robot = new Robot();
            Rectangle2D bounds = new Rectangle2D(x, y, width, height);
            WritableImage image = robot.getScreenCapture(null, bounds);

            File file = new File("screenshot.png");

            // 获取图像宽度和高度
            int imageWidth = (int) image.getWidth();
            int imageHeight = (int) image.getHeight();

            // 为图像创建缓冲区
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

            // 使用 PixelReader 读取每个像素并将其写入缓冲区
            PixelReader pixelReader = image.getPixelReader();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int argb = pixelReader.getArgb(i,j);
                    bufferedImage.setRGB(i, j, argb);
                }
            }

            ImageIO.write(bufferedImage, "png", file);
            String decode = QrCodeUtil.decode(bufferedImage);
            editTextArea.setText(decode);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}