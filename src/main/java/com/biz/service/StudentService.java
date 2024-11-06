package com.biz.service;

import com.biz.common.PageResult;
import com.biz.common.Result;
import com.biz.model.Student;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface StudentService {
    Student creat(Student student);

    PageResult<Student> findByConditions(Integer pageNum, Integer pageSize);

    Result<?> update(Student student);

    Result<?> delete(String id);
}
