package top.fuyuaaa.shadowpuppets.model.qo;

import lombok.Data;
import top.fuyuaaa.shadowpuppets.model.BaseModel;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-16 21:01
 */
@Data
@SuppressWarnings("all")
public class BaseQO extends BaseModel {
    private static final long serialVersionUID = -1576861533043829278L;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
