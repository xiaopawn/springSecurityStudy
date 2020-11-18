package com.pawn.courses.service;

import com.pawn.courses.model.PersonDemo;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class MethodELService {

    // 进入方法之前返回
    @PreAuthorize("hasRole('admin')")
    public List<PersonDemo> findAll(){
        return null;
    }

    // 进入方法之后返回
    @PostAuthorize("returnObject.name == authentication.name")
    public PersonDemo findOne(){
        String authName =
                getContext().getAuthentication().getName();
        System.out.println(authName);
        return new PersonDemo("admin");
    }

    // 进入方法之前匹配
    @PreFilter(filterTarget="ids", value="filterObject%2==0")
    public void delete(List<Integer> ids, List<String> usernames) {
        System.out.println(ids);
    }


    // 过滤器加载
    @PostFilter("filterObject.name == authentication.name")
    public List<PersonDemo> findAllPD(){

        List<PersonDemo> list = new ArrayList<>();
        list.add(new PersonDemo("kobe"));
        list.add(new PersonDemo("admin"));

        return list;
    }

}
