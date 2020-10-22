package entity;

import lombok.Data;


/**
 * @author : liuzg
 * @description todo
 * @date : 2020-10-22 13:14
 * @since 1.0
 **/
@Data
public class Invocation implements java.io.Serializable{

    private static final long serialVersionUID = -6849794470751667710L;

    private Long requestId;

    private String schoolId;

    private Object result;

    private String messageType;

    private String methodName;

    private Object[] parameters;

    private String targetClassBeanName;

    private Class<?> targetBeanClass;
}
