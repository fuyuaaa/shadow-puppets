package top.fuyuaaa.shadowpuppets.model.vo;

import lombok.Data;
import top.fuyuaaa.shadowpuppets.model.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fuyuaaa
 * @creat: 2019-05-01 15:44
 */
@Data
public class CourseVO implements Serializable {
    private static final long serialVersionUID = 853893204589797102L;
    private Integer id;
    private String courseName;
    private String courseIntroduction;
    private String mainImageUrl;
    private BigDecimal courseOriginPrice;
    private BigDecimal courseDiscountPrice;
    private String teacherName;
    private String teacherTel;
    private Integer courseHours;
    private Integer paidNumber;
    private String coursePlace;
    private String courseContent;
}
