package com.pawn.jwtserver.config.auth;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MyUserDetailsServiceMapper {

    @Select("select username,password,enable from sys_user where username = #{username} or phone = #{username}")
    MyUserDetails findByUserName(@Param("username") String username);

    @Select("select role_code from sys_role r left join sys_user_role sur on r.id = sur.role_id left join sys_user su on sur.user_id = su.id where su.username = #{username} or phone = #{username}")
    List<String> findRoleByUserName(@Param("username") String username);

    @Select("<script>" +
                "select url from sys_menu m left join sys_role_menu srm on m.id = srm.menu_id " +
                "left join sys_role sr on srm.role_id = sr.id where sr.role_code in " +
                "<foreach collection = 'roleCodes' item = 'roleCode' open = '(' close = ')' separator = ','>" +
                    "#{roleCode}" +
                "</foreach>" +
            "</script>")
    List<String> findAuthorityByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}
