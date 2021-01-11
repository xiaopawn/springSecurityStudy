package com.pawn.jwtserver.config.auth;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MyRBACServiceMapper {

    @Select("select url from sys_menu m left join sys_role_menu srm on m.id = srm.menu_id left join sys_role sr on srm.role_id = sr.id\n" +
            "left join sys_user_role sur on sr.id = sur.role_id left join sys_user su on sur.user_id = su.id\n" +
            "where su.username = #{username} or phone = #{username}")
    List<String> findUrlsByUserName(@Param("username") String username);
}
