package org.app.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.app.dao.User;
import org.app.model.req.SearchUserReq;

import java.util.List;

/**
 * @author Parker
 * @date 12/21/25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("<script>" +
            " select user_id from app_user  " +
            "<if test = 'searchType == 1'> " +
            " where mobile = #{keyWord} " +
            " </if>" +
            " <if test = 'searchType == 2'> " +
            "  where user_name = #{keyWord} " +
            " </if> " +
            " </script> ")
    List<String> searchUser(SearchUserReq req);
}
