package dto;

import lombok.Data;

/**
 * @author : liuzg
 * @description todo
 * @date : 2020-10-22 13:49
 * @since 1.0
 **/
@Data
public class HelloDTO implements java.io.Serializable{

    private static final long serialVersionUID = -1849794470751667710L;

    private String msg;

    private String remark;
}
