package com.changgou.user.dao;

import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    @Update("update tb_user set points = points + #{money} where username = #{username}")
    int updatePoints(Integer money,String username);


}
