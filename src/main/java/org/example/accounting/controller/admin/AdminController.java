package org.example.accounting.controller.admin;
import org.example.accounting.DO.*;
import org.example.accounting.service.AdminService;
import org.example.accounting.service.BillService;
import org.example.accounting.service.NotificationService;
import org.example.accounting.service.UserService;
import org.example.accounting.util.PageResult;
import org.example.accounting.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/allbills")
    public Result<PageResult> getAllBills(PageQueryDO pageQueryDO){
        PageResult pageresult = billService.pageQuery(pageQueryDO);
        Result<PageResult> result = new Result<>();
        result.setData(pageresult);
        result.setMessage("查询成功");
        result.setCode(200);

        return result;
    }

    @GetMapping("/users")
    public Result<PageResult> getAllUsers(PageQueryDO pageQueryDO){
        PageResult pageresult = userService.pageQuery(pageQueryDO);
        Result<PageResult> result = new Result<>();
        result.setData(pageresult);
        result.setMessage("查询成功");
        result.setCode(200);

        return result;
    }

    @PostMapping("/users/update")
    public Result updateUser(@RequestBody UserDO userDO){
        Result result = new Result();
        userService.updateUser(userDO);
        result.setCode(200);
        result.setMessage("修改成功");
        result.setData("修改成功");
        return result;
    }

    @PostMapping("/users/delete")
    public Result deleteUser(@RequestBody UserDO userDO){
        Result result = new Result();
        userService.deleteUser(userDO);
        result.setCode(200);
        result.setMessage("删除成功");
        result.setData("删除成功");

        return result;
    }

    @PostMapping("/settings/update")
    public Result updateSettings(@RequestBody SettingDO settingDO){
        Result result = new Result();
        adminService.updateSettings(settingDO);
        result.setCode(200);
        result.setMessage("修改成功");
        result.setData("修改成功");

        return result;
    }

    @GetMapping("/settings")
    public Result<SettingDO> getSetting(){
        Result<SettingDO> result = new Result<>();
        SettingDO settingDO = adminService.getSetting();
        result.setData(settingDO);
        result.setCode(200);

        return result;
    }

    @GetMapping("/notifications")
    public Result<PageResult> notifications(PageQueryDO pageQueryDO){
        PageResult pageresult = notificationService.pageQuery(pageQueryDO);
        Result<PageResult> result = new Result<>();
        result.setData(pageresult);
        result.setMessage("查询成功");
        result.setCode(200);

        return result;
    }

    @PostMapping("/deleteNotifications")
    public Result deleteNotifications(@RequestBody String id){
        Result result = new Result();
        notificationService.deleteNotifications(id);
        result.setCode(200);
        result.setMessage("删除成功");
        result.setData("删除成功");

        return result;
    }

    @PostMapping("/updateNotifications")
    public Result updateNotifications(@RequestBody Notification notification){
        Result result = new Result();
        notificationService.updateNotifications(notification);
        result.setCode(200);
        result.setMessage("修改成功");
        result.setData("修改成功");

        return result;
    }

    @PostMapping("/addNotifications")
    public Result addNotifications(@RequestBody Notification notification){
        Result result = new Result();
        notificationService.addNotifications(notification);
        result.setCode(200);
        result.setMessage("添加成功");
        result.setData("添加成功");

        return result;
    }

    @GetMapping("/categories")
    public Result getCategories(){
        Result result = new Result<>();
        List<Categories> categories = billService.getAllCategories();
        result.setData(categories);
        result.setCode(200);
        result.setMessage("类别查询成功");

        return result;
    }

    @PostMapping("/categories/add")
    public Result addCategories(@RequestBody Categories categories){
        Result result = new Result();
        billService.addCategories(categories);
        result.setData("添加成功");
        result.setCode(200);
        result.setMessage("添加成功");

        return result;
    }

    @PostMapping("/categories/delete")
    public Result deleteCategories(@RequestBody Categories categories){
        Result result = new Result();
        billService.deleteCategories(categories);
        result.setCode(200);
        result.setMessage("删除成功");
        result.setData("删除成功");

        return result;
    }

    @PostMapping("/categories/update")
    public Result updateCategories(@RequestBody Categories categories){
        Result result = new Result();
        billService.updateCategories(categories);
        result.setData("修改成功");
        result.setCode(200);
        result.setMessage("修改成功");

        return result;
    }

}
