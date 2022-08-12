package com.fzz.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long id = BaseContext.getCurrent();
        metaObject.setValue("createUser", id);
        metaObject.setValue("updateUser", id);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long id = BaseContext.getCurrent();
        metaObject.setValue("updateUser", id);
        metaObject.setValue("updateTime", LocalDateTime.now());

    }
}
