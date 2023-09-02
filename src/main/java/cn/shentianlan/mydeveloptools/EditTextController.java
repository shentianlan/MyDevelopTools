package cn.shentianlan.mydeveloptools;

import cn.hutool.core.util.StrUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * 编辑文本控制器
 *
 * @author shentianlan
 * @version 1.0
 */
public class EditTextController {

    @FXML
    private TextArea editTextArea;

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
}