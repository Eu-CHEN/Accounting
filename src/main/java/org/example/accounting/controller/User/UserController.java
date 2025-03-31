package org.example.accounting.controller.User;

import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.UserDO;
import org.example.accounting.DO.UserLoginDO;
import org.example.accounting.constant.JwtClaimsConstant;
import org.example.accounting.model.BillStatistics;
import org.example.accounting.model.User;
import org.example.accounting.properties.JwtProperties;
import org.example.accounting.service.BillService;
import org.example.accounting.service.NotificationService;
import org.example.accounting.service.UserService;
import org.example.accounting.util.JwtUtil;
import org.example.accounting.util.PageResult;
import org.example.accounting.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BillService billService;

    @PostMapping("/host")
    public Result<User> postUser(@RequestBody UserDO user1) {
        Result result = new Result();
        User user = userService.login(user1);
        if (user == null){
            result.setCode(400);
            result.setMessage("没有该用户");
        }else {
            //生成jwt令牌
            Map<String,Object> claims = new HashMap<>();
            if (user.getUsertype().equals("user")){
                claims.put(JwtClaimsConstant.USER_ID, user.getId());
                String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
                UserLoginDO build = UserLoginDO.builder()
                        .id(user.getId())
                        .token(token)
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .addTime(user.getAddTime())
                        .usertype(user.getUsertype())
                        .build();
                result.setCode(200);
                result.setMessage("登录成功");
                result.setData(build);
            }else if (user.getUsertype().equals("admin")){
                claims.put(JwtClaimsConstant.USER_ID, user.getId());
                String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(),jwtProperties.getAdminTtl(),claims);
                UserLoginDO build = UserLoginDO.builder()
                        .id(user.getId())
                        .token(token)
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .addTime(user.getAddTime())
                        .usertype(user.getUsertype())
                        .build();
                result.setCode(200);
                result.setMessage("欢迎管理员");
                result.setData(build);
            }
        }
                return result;
    }

    @PostMapping("/host/adduser")
    public Result<User> addUser(@RequestBody UserDO userDO) {
        Result<User> result = new Result();
        if(userDO.getUsername()=="" || userDO.getPassword()=="" || userDO.getEmail() == "" || userDO.getPhone() == ""){
            result.setCode(400);
            result.setData(null);
            result.setMessage("有必填项未填");
        }else {
            result.setCode(200);
            result.setMessage("欢迎宝宝子");
            result.setData(userService.addUser(userDO));
        }
        return result;
    }

    @PostMapping("/login/user/userupdata")
    public Result<User> UpdateUser(@RequestBody UserDO userDO) {
        Result<User> result = new Result();
        userService.updateUser(userDO);
        result.setCode(200);
        result.setData(userDO.toModel(userDO));
        result.setMessage("修改成功");
        return result;
    }

    @GetMapping("/login/notice/list")
    public Result<PageResult> noticeList(PageQueryDO pageQueryDO) {
        Result<PageResult> result = new Result();
        PageResult pageResult = notificationService.pageQuery(pageQueryDO);
        result.setData(pageResult);
        result.setMessage("查询成功");
        result.setCode(200);

        return result;
    }

    @GetMapping("/login/bill/summary")
    public Result<BillStatistics> getBillSummary(String username,String timeRange) {
        BillStatistics billStatistics1 = billService.getBillSummary(username,timeRange);
        Result<BillStatistics> result = new Result();
        result.setData(billStatistics1);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }
}
