package com.biz.service.impl;

import com.biz.common.HttpStatus;
import com.biz.common.PageResult;
import com.biz.common.Result;
import com.biz.model.Student;
import com.biz.service.StudentService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService {
    public Student creat(Student student) {
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
        return student;
    }

    @Override
    public PageResult<Student> findByConditions(Integer pageNum, Integer pageSize) {
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

    @Override
    public Result<?> update(Student student) {
        Jedis jedis = new Jedis("192.168.101.128", 6379);
        jedis.auth("123456");
        String key = "student:" + student.getId();
        jedis.hset(key, "name", student.getName());
        jedis.hset(key, "birthday", student.getBirthday());
        jedis.hset(key, "description", student.getDescription());
        jedis.zadd("student_avgScore", student.getAvgScore(), key);
        return new Result<>(HttpStatus.SUCCESS, "更新成功");
    }

    @Override
    public Result<?> delete(String id) {
        Jedis jedis = new Jedis("192.168.101.128", 6379);
        jedis.auth("123456");
        String key = "student:" + id;
        jedis.del(key);
        jedis.zrem("student_avgScore", key);

        return new Result<>(HttpStatus.SUCCESS, "删除成功");
    }
}
