package com.atguigu.demo.controller;

/**
 * @date 2024-01-26
 */

import com.atguigu.demo.bean.Employee;
import com.atguigu.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    // service= new EmployeeServiceImpl()
    @Autowired
    private EmployeeService service;
    /*
        http://localhost:8080/emp?op=delete&id=1&lastname=2&gender=3&email=4
     */
    @RequestMapping("/emp")
    public Object handle1(String op,Integer id,String lastname,String gender,String email){
        //封装数据模型
        Employee employee = new Employee(id, lastname, gender, email);

        switch (op){
            case "select": if (id == null){
                return "id不合法!";
            }else {
                //查询的员工不存在
                Employee e = service.getEmpById(id);
                return e == null ? "此人不存在!" : e;
            }

            case "delete": if (id == null){
                return "id不合法!";
            }else {
                service.deleteEmpById(id);
                return "ok";
            }

            case "update": if (id == null){
                return "id不合法!";
            }else {
                service.updateEmp(employee);
                return "ok";
            }

            case "insert": {
                service.insertEmp(employee);
                return "ok";
            }
            default: return "ok";
        }

    }

    /*
      http://localhost:8080/getAllEmp
     */
    @RequestMapping("/getAllEmp")
    public Object handle2(){
        return  service.getAll();
    }
}
