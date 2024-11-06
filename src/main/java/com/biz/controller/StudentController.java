package com.biz.controller;

import com.biz.common.PageResult;
import com.biz.common.Result;
import com.biz.model.Student;
import com.biz.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;




@RestController()
@RequestMapping("/student")
@Slf4j
@CrossOrigin
public class StudentController {

    @Resource
    private StudentService studentService;


    /**
     * 添加学生
     * @param student Student
     * @return Result<Student>
     */
    @PostMapping
    public Result<Student> create(@RequestBody Student student) {
        return new Result<>(studentService.creat(student));
    }


    /**
     * 分页查询
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return PageResult<Student>
     */
    @GetMapping()
    public PageResult<Student> findByConditions(@RequestParam(required = false) Integer pageNum,
                                                @RequestParam(required = false) Integer pageSize) {
        return studentService.findByConditions(pageNum, pageSize);
    }


    /**
     * 更新学生信息
     * @param student Student
     * @return 操作结果
     */
    @PatchMapping
    public Result<?> update(@RequestBody Student student) {
        return studentService.update(student);
    }

    /**
     * 删除学生信息
     * @param id 删除学生id
     * @return 操作结果
     */
    @DeleteMapping
    public Result<?> delete(@RequestParam String id) {
        return studentService.delete(id);
    }
}
