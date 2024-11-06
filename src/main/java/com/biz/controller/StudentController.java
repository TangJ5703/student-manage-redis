package com.biz.controller;

import com.biz.common.HttpStatus;
import com.biz.common.PageResult;
import com.biz.common.Result;
import com.biz.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



@RestController()
@RequestMapping("/student")
@Slf4j
@CrossOrigin
public class StudentController {


    /**
     * 添加学生
     * @param student Student
     * @return Result<Student>
     */
    @PostMapping
    public Result<Student> create(@RequestBody Student student) {
        Jedis jedis = new Jedis("192.168.101.128", 6379);
        jedis.auth("123456");

        String key = "student:" + student.getId();
        jedis.hset(key, "id", student.getId());
        jedis.hset(key, "name", student.getName());
        jedis.hset(key, "birthday", student.getBirthday());
        jedis.hset(key, "description", student.getDescription());
        jedis.hset(key, "avgScore", String.valueOf(student.getAvgScore()));

        // 将学生加入 ZSET 排序集合，按 avgScore 排序
        jedis.zadd("student_avgScore", student.getAvgScore(), key);
        return new Result<>(student);
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
        Jedis jedis = new Jedis("192.168.101.128", 6379);
        jedis.auth("123456");
        if (pageNum == null || pageSize == null) {
            pageNum = 1;
            pageSize = 10;
        }
        long start = (long) (pageNum - 1) * pageSize;
        long end = start + pageSize - 1;

        Set<Tuple> studentsWithScoresSet = jedis.zrevrangeWithScores("student_avgScore", start, end);
        List<Tuple> studentsWithScores = new ArrayList<>(studentsWithScoresSet);

        // 获取学生的详细信息并转换为 Student 对象
        List<Student> studentList = new ArrayList<>();
        for (Tuple student : studentsWithScores) {
            String studentId = student.getElement();

            // 获取保存在Hash中的学生详细信息
            Map<String, String> studentDetails = jedis.hgetAll(studentId);

            // 创建 Student 对象并填充字段
            Student studentObj = new Student();
            studentObj.setId(studentId.substring(8));
            studentObj.setName(studentDetails.get("name"));
            studentObj.setBirthday(studentDetails.get("birthday"));
            studentObj.setDescription(studentDetails.get("description"));
            studentObj.setAvgScore((int) student.getScore());

            studentList.add(studentObj);
        }

        // 获取总记录数
        long total = jedis.zcard("student_avgScore");

        // 创建 PageResult 对象并返回
        return new PageResult<>(studentList, total, pageNum, pageSize);
    }


    /**
     * 更新学生信息
     * @param student Student
     * @return 操作结果
     */
    @PatchMapping
    public Result<?> update(@RequestBody Student student) {
        Jedis jedis = new Jedis("192.168.101.128", 6379);
        jedis.auth("123456");
        String key = "student:" + student.getId();
        jedis.hset(key, "name", student.getName());
        jedis.hset(key, "birthday", student.getBirthday());
        jedis.hset(key, "description", student.getDescription());
        jedis.zadd("student_avgScore", student.getAvgScore(), key);
        return new Result<>(HttpStatus.SUCCESS, "更新成功");
    }

    /**
     * 删除学生信息
     * @param id 删除学生id
     * @return 操作结果
     */
    @DeleteMapping
    public Result<?> delete(@RequestParam String id) {

        Jedis jedis = new Jedis("192.168.101.128", 6379);
            jedis.auth("123456");
            String key = "student:" + id;
            jedis.del(key);
            jedis.zrem("student_avgScore", key);

        return new Result<>(HttpStatus.SUCCESS, "删除成功");
    }
}
